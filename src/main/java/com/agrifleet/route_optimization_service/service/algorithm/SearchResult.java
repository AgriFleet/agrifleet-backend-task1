package com.agrifleet.route_optimization_service.service.algorithm;

import java.util.List;

/**
 * Outcome of a shortest-path search.
 *
 * @param found             whether a path to the target was discovered
 * @param path              ordered node ids start -> target (empty when not found)
 * @param edges             ordered {@link GraphEdge}s used along the path
 * @param totalCostMinutes  total travel cost in minutes
 * @param totalDistanceKm   total physical distance in km
 * @param nodeExpansions    vertices popped from the heap (benchmark metric)
 */
public record SearchResult(
        boolean found,
        List<Long> path,
        List<GraphEdge> edges,
        double totalCostMinutes,
        double totalDistanceKm,
        int nodeExpansions
) {
    public static SearchResult notFound(int nodeExpansions) {
        return new SearchResult(false, List.of(), List.of(), 0.0, 0.0, nodeExpansions);
    }
}
