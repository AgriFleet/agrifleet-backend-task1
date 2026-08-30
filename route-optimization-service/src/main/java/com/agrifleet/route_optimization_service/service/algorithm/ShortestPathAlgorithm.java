package com.agrifleet.route_optimization_service.service.algorithm;

/**
 * Contract for single-source shortest-path algorithms over the {@link RoutingGraph}.
 */
public interface ShortestPathAlgorithm {

    /**
     * @param graph  directed weighted graph
     * @param start  start node id
     * @param target target node id
     * @return the optimal path or {@link SearchResult#notFound(int)} when unreachable
     */
    SearchResult findShortestPath(RoutingGraph graph, long start, long target);
}
