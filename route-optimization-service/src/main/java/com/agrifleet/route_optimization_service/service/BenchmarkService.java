package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.dto.BenchmarkRequest;
import com.agrifleet.route_optimization_service.dto.BenchmarkResponse;
import com.agrifleet.route_optimization_service.dto.BenchmarkResultDto;
import com.agrifleet.route_optimization_service.exception.BadRequestException;
import com.agrifleet.route_optimization_service.model.RouteRequest;
import com.agrifleet.route_optimization_service.service.algorithm.AStarAlgorithm;
import com.agrifleet.route_optimization_service.service.algorithm.DijkstraAlgorithm;
import com.agrifleet.route_optimization_service.service.algorithm.Haversine;
import com.agrifleet.route_optimization_service.service.algorithm.NodeCoord;
import com.agrifleet.route_optimization_service.service.algorithm.RoutingGraph;
import com.agrifleet.route_optimization_service.service.algorithm.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates the Chapter 8 experimental performance data: runs A* / Dijkstra on
 * synthetic random graphs of increasing size (V) and reports average execution
 * time (ms) and node expansions per size. Fully in-memory (no DB writes) and
 * deterministic (fixed RNG seed) so results are reproducible for the report.
 */
@Service
@RequiredArgsConstructor
public class BenchmarkService {

    private static final long RANDOM_SEED = 42L;
    private static final double MAX_SPEED_KMH = 60.0;
    private static final double BASE_LAT = 7.2906; // Kandy region
    private static final double BASE_LON = 80.6337;

    private final AStarAlgorithm aStar;
    private final DijkstraAlgorithm dijkstra;

    public BenchmarkResponse run(BenchmarkRequest request) {
        if (request.sizes() == null || request.sizes().isEmpty()) {
            throw new BadRequestException("sizes must contain at least one value");
        }
        if (request.sizes().stream().anyMatch(s -> s == null || s < 2)) {
            throw new BadRequestException("each graph size must be at least 2");
        }
        int runs = request.runsPerSize() != null && request.runsPerSize() > 0
                ? request.runsPerSize() : 3;
        RouteRequest.Algorithm algorithm = request.algorithm() != null
                ? request.algorithm() : RouteRequest.Algorithm.ASTAR;

        List<BenchmarkResultDto> results = new ArrayList<>();
        for (int size : request.sizes()) {
            double totalMs = 0.0;
            long totalExpansions = 0;
            for (int i = 0; i < runs; i++) {
                RoutingGraph graph = generateGraph(size);
                long t0 = System.nanoTime();
                SearchResult result = run(algorithm, graph, 0, size - 1);
                totalMs += (System.nanoTime() - t0) / 1_000_000.0;
                totalExpansions += result.nodeExpansions();
            }
            results.add(new BenchmarkResultDto(size,
                    round2(totalMs / runs),
                    Math.round(totalExpansions / (double) runs)));
        }
        return new BenchmarkResponse(results, "O((V + E) log V)");
    }

    private SearchResult run(RouteRequest.Algorithm algorithm, RoutingGraph graph,
                             long start, long end) {
        return algorithm == RouteRequest.Algorithm.DIJKSTRA
                ? dijkstra.findShortestPath(graph, start, end)
                : aStar.findShortestPath(graph, start, end);
    }

    /** Deterministic random graph: spanning chain (connectivity) + random edges. */
    private RoutingGraph generateGraph(int vertices) {
        Random random = new Random(RANDOM_SEED);
        RoutingGraph graph = new RoutingGraph();

        for (int i = 0; i < vertices; i++) {
            double lat = BASE_LAT + (random.nextDouble() - 0.5) * 0.6;
            double lon = BASE_LON + (random.nextDouble() - 0.5) * 0.6;
            graph.addNode(i, lat, lon);
        }

        // Guarantee connectivity with a spanning chain, then add random edges (~3x degree).
        for (int i = 1; i < vertices; i++) {
            addRandomEdge(graph, random, i - 1, i);
        }
        int extraEdges = vertices * 2;
        for (int k = 0; k < extraEdges; k++) {
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);
            if (u != v) {
                addRandomEdge(graph, random, u, v);
            }
        }

        graph.setMaxSpeedKmh(MAX_SPEED_KMH);
        return graph;
    }

    private void addRandomEdge(RoutingGraph graph, Random random, int u, int v) {
        NodeCoord a = graph.coords().get((long) u);
        NodeCoord b = graph.coords().get((long) v);
        double km = Haversine.distanceKm(a.latitude(), a.longitude(), b.latitude(), b.longitude());
        double speed = 20 + random.nextInt(41); // 20..60 km/h
        double minutes = (km / speed) * 60.0;
        long edgeId = (long) u * 1_000_000L + v;
        graph.addEdge(u, v, minutes, km, edgeId);
        graph.addEdge(v, u, minutes, km, edgeId);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
