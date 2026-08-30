package com.agrifleet.route_optimization_service.service.algorithm;

/**
 * A directed adjacency-list entry used by the routing algorithms.
 *
 * @param to           target node id
 * @param costMinutes  travel cost in minutes (after surface/weather/vehicle factors)
 * @param distanceKm   physical road length in kilometres
 * @param roadEdgeId   backing road_edge row (for tracing/debugging)
 */
public record GraphEdge(long to, double costMinutes, double distanceKm, long roadEdgeId) {
}
