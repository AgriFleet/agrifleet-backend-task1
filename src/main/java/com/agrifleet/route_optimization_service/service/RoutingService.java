package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.algorithm.AStarRouter;
import com.agrifleet.route_optimization_service.entity.RoadEdgeEntity;
import com.agrifleet.route_optimization_service.entity.RoadNodeEntity;
import com.agrifleet.route_optimization_service.repository.RoadEdgeRepository;
import com.agrifleet.route_optimization_service.repository.RoadNodeRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoutingService {

    private final RoadNodeRepository nodeRepository;
    private final RoadEdgeRepository edgeRepository;
    private final RouteExecutionCacheService cacheService;

    public RoutingService(RoadNodeRepository nodeRepository,
                          RoadEdgeRepository edgeRepository,
                          RouteExecutionCacheService cacheService) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.cacheService = cacheService;
    }

    public Map<String, Object> calculateRoute(Long startId, Long targetId, boolean useHeuristic, Long vehicleId) {
        List<RoadNodeEntity> nodes = nodeRepository.findAll();
        List<RoadEdgeEntity> edges = edgeRepository.findAll();

        Map<Long, RoadNodeEntity> nodeMap = nodes.stream()
                .collect(Collectors.toMap(RoadNodeEntity::getNodeId, n -> n));

        Map<Long, List<RoadEdgeEntity>> adjacencyList = edges.stream()
                .collect(Collectors.groupingBy(RoadEdgeEntity::getUNode));

        AStarRouter.PathResult result = AStarRouter.computeShortestPath(startId, targetId, nodeMap, adjacencyList, useHeuristic);

        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", useHeuristic ? "ASTAR" : "DIJKSTRA");
        response.put("startNode", startId);
        response.put("targetNode", targetId);
        response.put("pathNodeSequence", result.pathSequence);
        response.put("totalDistanceKm", result.totalDistance);
        response.put("totalTravelTimeMins", result.totalDistance * 2.5);
        response.put("nodesVisitedCount", result.nodesVisited);

        try {
            cacheService.saveCacheEntry(vehicleId, startId, targetId, response);
        } catch (Exception ignored) {
        }

        return response;
    }
}