package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.dto.AlgorithmMetricsDto;
import com.agrifleet.route_optimization_service.dto.CompareResultDto;
import com.agrifleet.route_optimization_service.dto.OptimizeRouteRequest;
import com.agrifleet.route_optimization_service.dto.PathPointDto;
import com.agrifleet.route_optimization_service.dto.PathTracingResponse;
import com.agrifleet.route_optimization_service.dto.RouteResponse;
import com.agrifleet.route_optimization_service.dto.RouteSummaryDto;
import com.agrifleet.route_optimization_service.dto.TracePointDto;
import com.agrifleet.route_optimization_service.exception.BadRequestException;
import com.agrifleet.route_optimization_service.exception.ResourceNotFoundException;
import com.agrifleet.route_optimization_service.model.RoadEdge;
import com.agrifleet.route_optimization_service.model.RoadEdge.SurfaceType;
import com.agrifleet.route_optimization_service.model.RoadNode;
import com.agrifleet.route_optimization_service.model.RoutePathSegment;
import com.agrifleet.route_optimization_service.model.RouteRequest;
import com.agrifleet.route_optimization_service.model.RouteResult;
import com.agrifleet.route_optimization_service.model.Vehicle;
import com.agrifleet.route_optimization_service.model.WeatherCondition;
import com.agrifleet.route_optimization_service.repository.RoadEdgeRepository;
import com.agrifleet.route_optimization_service.repository.RoadNodeRepository;
import com.agrifleet.route_optimization_service.repository.RoutePathSegmentRepository;
import com.agrifleet.route_optimization_service.repository.RouteRequestRepository;
import com.agrifleet.route_optimization_service.repository.RouteResultRepository;
import com.agrifleet.route_optimization_service.repository.VehicleRepository;
import com.agrifleet.route_optimization_service.repository.WeatherConditionRepository;
import com.agrifleet.route_optimization_service.service.algorithm.AStarAlgorithm;
import com.agrifleet.route_optimization_service.service.algorithm.DijkstraAlgorithm;
import com.agrifleet.route_optimization_service.service.algorithm.RoutingGraph;
import com.agrifleet.route_optimization_service.service.algorithm.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates Task 1 routing: builds the weighted {@link RoutingGraph} from the
 * persisted road network (applying surface friction, weather resistance, vehicle
 * speed factor and bridge weight pruning), runs A* / Dijkstra, persists the
 * request/result/path, and exposes route summaries + GPS path tracing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final RoadNodeRepository nodeRepository;
    private final RoadEdgeRepository edgeRepository;
    private final WeatherConditionRepository weatherRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteRequestRepository requestRepository;
    private final RouteResultRepository resultRepository;
    private final RoutePathSegmentRepository segmentRepository;
    private final CostModel costModel;
    private final AStarAlgorithm aStar;
    private final DijkstraAlgorithm dijkstra;

    // ------------------------------------------------------------------
    // Core optimization
    // ------------------------------------------------------------------

    @Transactional
    public RouteResponse optimize(OptimizeRouteRequest request) {
        validateRequest(request);

        RoadNode start = findNode(request.startNodeId());
        RoadNode end = findNode(request.endNodeId());
        Vehicle vehicle = request.vehicleId() != null ? findVehicle(request.vehicleId()) : null;

        double weight = resolveWeight(request, vehicle);
        double speedFactor = vehicle != null && vehicle.getSpeedFactor() != null
                ? vehicle.getSpeedFactor() : 1.0;
        boolean weatherAware = Boolean.TRUE.equals(request.weatherAware());
        RouteRequest.Algorithm algorithm = request.algorithm() != null
                ? request.algorithm() : RouteRequest.Algorithm.ASTAR;

        GraphBuild build = buildGraph(weight, speedFactor, weatherAware);

        long t0 = System.nanoTime();
        SearchResult result = runAlgorithm(algorithm, build.graph(),
                request.startNodeId(), request.endNodeId());
        double executionMs = (System.nanoTime() - t0) / 1_000_000.0;

        RouteResult.Status status;
        if (result.found()) {
            status = RouteResult.Status.FOUND;
        } else if (build.prunedBridges() > 0) {
            status = RouteResult.Status.BLOCKED;
        } else {
            status = RouteResult.Status.NOT_FOUND;
        }

        List<String> warnings = new ArrayList<>();
        if (build.prunedBridges() > 0) {
            warnings.add(build.prunedBridges() + " bridge(s) skipped: vehicle weight exceeds bridge tolerance");
        }
        if (!result.found()) {
            warnings.add("No path found from node " + request.startNodeId() + " to node " + request.endNodeId());
        }

        RouteRequest persistedRequest = requestRepository.save(RouteRequest.builder()
                .vehicle(vehicle)
                .startNode(start)
                .endNode(end)
                .algorithm(algorithm)
                .weatherAware(weatherAware)
                .weightUnitTonnes(weight)
                .build());

        RouteResult routeResult = RouteResult.builder()
                .request(persistedRequest)
                .status(status)
                .totalDistanceKm(round2(result.totalDistanceKm()))
                .totalTravelTimeMin(round2(result.totalCostMinutes()))
                .nodeExpansions(result.nodeExpansions())
                .executionTimeMs(round2(executionMs))
                .build();

        List<PathPointDto> pathPoints = buildPathPoints(routeResult, result, build.nodesById());
        RouteResult saved = resultRepository.save(routeResult);

        log.info("Route {} -> {} [{}]: {} km, {} min, {} expansions, {} ms -> {}",
                request.startNodeId(), request.endNodeId(), algorithm,
                saved.getTotalDistanceKm(), saved.getTotalTravelTimeMin(),
                saved.getNodeExpansions(), saved.getExecutionTimeMs(), status);

        return new RouteResponse(saved.getId(), algorithm.name(), status,
                saved.getTotalDistanceKm(), saved.getTotalTravelTimeMin(),
                saved.getNodeExpansions(), saved.getExecutionTimeMs(),
                pathPoints, warnings);
    }

    // ------------------------------------------------------------------
    // Route history & path tracing
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public RouteSummaryDto getRoute(Long resultId) {
        return toSummary(findResult(resultId));
    }

    @Transactional(readOnly = true)
    public List<RouteSummaryDto> listRoutes(Long vehicleId, RouteResult.Status status,
                                            RouteRequest.Algorithm algorithm) {
        List<RouteResult> results;
        if (vehicleId != null) {
            results = resultRepository.findByRequest_Vehicle_Id(vehicleId);
        } else if (status != null) {
            results = resultRepository.findByStatus(status);
        } else if (algorithm != null) {
            results = resultRepository.findByRequest_Algorithm(algorithm);
        } else {
            results = resultRepository.findAll();
        }
        return results.stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public PathTracingResponse getPath(Long resultId) {
        RouteResult routeResult = findResult(resultId);
        List<RoutePathSegment> segments =
                segmentRepository.findByRouteResultIdOrderBySequenceAsc(resultId);
        List<TracePointDto> points = segments.stream()
                .map(s -> new TracePointDto(
                        s.getSequence(),
                        s.getNode() != null ? s.getNode().getId() : null,
                        s.getLatitude(),
                        s.getLongitude(),
                        s.getDistanceFromStartKm(),
                        s.getCumulativeTimeMin()))
                .toList();
        return new PathTracingResponse(routeResult.getId(), routeResult.getStatus(),
                routeResult.getTotalDistanceKm(), points);
    }

    // ------------------------------------------------------------------
    // A* vs Dijkstra benchmark
    // ------------------------------------------------------------------

    @Transactional
    public CompareResultDto compare(OptimizeRouteRequest request) {
        validateRequest(request);
        Vehicle vehicle = request.vehicleId() != null ? findVehicle(request.vehicleId()) : null;
        double weight = resolveWeight(request, vehicle);
        double speedFactor = vehicle != null && vehicle.getSpeedFactor() != null
                ? vehicle.getSpeedFactor() : 1.0;
        boolean weatherAware = Boolean.TRUE.equals(request.weatherAware());

        RoutingGraph graph = buildGraph(weight, speedFactor, weatherAware).graph();

        long t0 = System.nanoTime();
        SearchResult a = aStar.findShortestPath(graph, request.startNodeId(), request.endNodeId());
        double aMs = (System.nanoTime() - t0) / 1_000_000.0;
        long t1 = System.nanoTime();
        SearchResult d = dijkstra.findShortestPath(graph, request.startNodeId(), request.endNodeId());
        double dMs = (System.nanoTime() - t1) / 1_000_000.0;

        int vertices = graph.vertexCount();
        AlgorithmMetricsDto aStarMetrics = toMetrics(a, aMs, vertices);
        AlgorithmMetricsDto dijkstraMetrics = toMetrics(d, dMs, vertices);

        String conclusion;
        if (!a.found() && !d.found()) {
            conclusion = "No path exists between the given nodes";
        } else {
            double reduction = 100.0 * (d.nodeExpansions() - a.nodeExpansions())
                    / Math.max(1, d.nodeExpansions());
            conclusion = String.format("A* expanded %.1f%% fewer nodes than Dijkstra on this query", reduction);
        }
        return new CompareResultDto(request.startNodeId(), request.endNodeId(),
                aStarMetrics, dijkstraMetrics, conclusion);
    }

    // ------------------------------------------------------------------
    // Graph construction
    // ------------------------------------------------------------------

    /** Builds the weighted directed graph, applying surface/weather costs and bridge pruning. */
    private GraphBuild buildGraph(double weight, double speedFactor, boolean weatherAware) {
        RoutingGraph graph = new RoutingGraph();
        Map<Long, RoadNode> nodesById = new HashMap<>();
        Map<Long, WeatherCondition> weatherByNode = new HashMap<>();
        double maxSpeed = 0.0;
        int prunedBridges = 0;

        for (RoadNode node : nodeRepository.findByIsActiveTrue()) {
            nodesById.put(node.getId(), node);
            graph.addNode(node.getId(), node.getLatitude(), node.getLongitude());
        }
        for (WeatherCondition weather : weatherRepository.findAll()) {
            if (weather.getNode() != null) {
                weatherByNode.put(weather.getNode().getId(), weather);
            }
        }

        for (RoadEdge edge : edgeRepository.findByIsActiveTrue()) {
            maxSpeed = Math.max(maxSpeed, edge.getSpeedLimitKmh());

            // Machinery weight restriction: prune overloaded bridges
            if (edge.getWeightLimitTonnes() != null && weight > edge.getWeightLimitTonnes()) {
                prunedBridges++;
                continue;
            }

            double baseMinutes = costModel.travelTimeMinutes(edge.getDistanceKm(), edge.getSpeedLimitKmh());
            double surfaceFactor = costModel.surfaceFactor(edge.getSurfaceType());

            double forwardCost = edgeCost(baseMinutes, surfaceFactor, weatherAware,
                    edge.getSurfaceType(), weatherByNode.get(edge.getTargetNode().getId()), speedFactor);
            graph.addEdge(edge.getSourceNode().getId(), edge.getTargetNode().getId(),
                    forwardCost, edge.getDistanceKm(), edge.getId());

            if (Boolean.TRUE.equals(edge.getIsBidirectional())) {
                double reverseCost = edgeCost(baseMinutes, surfaceFactor, weatherAware,
                        edge.getSurfaceType(), weatherByNode.get(edge.getSourceNode().getId()), speedFactor);
                graph.addEdge(edge.getTargetNode().getId(), edge.getSourceNode().getId(),
                        reverseCost, edge.getDistanceKm(), edge.getId());
            }
        }

        graph.setMaxSpeedKmh(maxSpeed);
        return new GraphBuild(graph, nodesById, prunedBridges);
    }

    private double edgeCost(double baseMinutes, double surfaceFactor, boolean weatherAware,
                            SurfaceType surface, WeatherCondition weather, double speedFactor) {
        double multiplier = 1.0;
        if (weatherAware && costModel.isUnpaved(surface) && weather != null) {
            double intensity = weather.getRainIntensity() != null ? weather.getRainIntensity() : 0.0;
            multiplier = costModel.weatherMultiplier(weather.getCondition(), intensity);
        }
        return baseMinutes * surfaceFactor * multiplier / speedFactor;
    }

    private List<PathPointDto> buildPathPoints(RouteResult routeResult, SearchResult result,
                                               Map<Long, RoadNode> nodesById) {
        List<PathPointDto> points = new ArrayList<>();
        if (!result.found()) {
            return points;
        }
        List<Long> path = result.path();
        double cumulativeKm = 0.0;
        double cumulativeMin = 0.0;
        for (int i = 0; i < path.size(); i++) {
            RoadNode node = nodesById.get(path.get(i));
            if (i > 0) {
                var edge = result.edges().get(i - 1);
                cumulativeKm += edge.distanceKm();
                cumulativeMin += edge.costMinutes();
            }
            routeResult.getPathSegments().add(RoutePathSegment.builder()
                    .routeResult(routeResult)
                    .sequence(i)
                    .node(node)
                    .latitude(node.getLatitude())
                    .longitude(node.getLongitude())
                    .distanceFromStartKm(round2(cumulativeKm))
                    .cumulativeTimeMin(round2(cumulativeMin))
                    .build());
            points.add(new PathPointDto(node.getId(), node.getName(),
                    node.getLatitude(), node.getLongitude(),
                    round2(cumulativeKm), round2(cumulativeMin)));
        }
        return points;
    }

    private SearchResult runAlgorithm(RouteRequest.Algorithm algorithm, RoutingGraph graph,
                                      long start, long end) {
        return switch (algorithm) {
            case ASTAR -> aStar.findShortestPath(graph, start, end);
            case DIJKSTRA -> dijkstra.findShortestPath(graph, start, end);
        };
    }

    private AlgorithmMetricsDto toMetrics(SearchResult result, double executionMs, int vertices) {
        double searchSpacePct = vertices > 0
                ? 100.0 * result.nodeExpansions() / vertices : 0.0;
        return new AlgorithmMetricsDto(result.found(),
                round2(result.totalDistanceKm()),
                round2(result.totalCostMinutes()),
                result.nodeExpansions(),
                round2(executionMs),
                round2(searchSpacePct));
    }

    private RouteSummaryDto toSummary(RouteResult result) {
        RouteRequest request = result.getRequest();
        return new RouteSummaryDto(
                result.getId(),
                request.getId(),
                request.getAlgorithm().name(),
                result.getStatus(),
                result.getTotalDistanceKm(),
                result.getTotalTravelTimeMin(),
                result.getNodeExpansions(),
                result.getExecutionTimeMs(),
                result.getCreatedAt() != null ? result.getCreatedAt().toString() : null);
    }

    private double resolveWeight(OptimizeRouteRequest request, Vehicle vehicle) {
        if (request.weightUnitTonnes() != null) {
            return request.weightUnitTonnes();
        }
        return vehicle != null && vehicle.getMaxWeightTonnes() != null
                ? vehicle.getMaxWeightTonnes() : 0.0;
    }

    private void validateRequest(OptimizeRouteRequest request) {
        if (request.startNodeId() == null || request.endNodeId() == null) {
            throw new BadRequestException("startNodeId and endNodeId are required");
        }
        if (request.startNodeId().equals(request.endNodeId())) {
            throw new BadRequestException("start and end node must be different");
        }
    }

    private RoadNode findNode(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Road node with id " + id + " does not exist"));
    }

    private Vehicle findVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle with id " + id + " does not exist"));
    }

    private RouteResult findResult(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route result with id " + id + " does not exist"));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /** Immutable bundle produced while building the in-memory graph. */
    private record GraphBuild(RoutingGraph graph, Map<Long, RoadNode> nodesById, int prunedBridges) {
    }
}
