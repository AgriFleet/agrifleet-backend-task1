package com.agrifleet.route_optimization_service.model;

import java.util.List;

public class RouteResult {
    private final List<Integer> pathNodeIds;
    private final double totalDistance;
    private final int nodesVisited;
    private final long executionTimeMs;

    public RouteResult(List<Integer> pathNodeIds, double totalDistance, int nodesVisited, long executionTimeMs) {
        this.pathNodeIds = pathNodeIds;
        this.totalDistance = totalDistance;
        this.nodesVisited = nodesVisited;
        this.executionTimeMs = executionTimeMs;
    }

    public List<Integer> getPathNodeIds() { return pathNodeIds; }
    public double getTotalDistance() { return totalDistance; }
    public int getNodesVisited() { return nodesVisited; }
    public long getExecutionTimeMs() { return executionTimeMs; }
}
