package com.agrifleet.route_optimization_service.model;

public class Edge {
    private final int targetNodeId;
    private final double weight;

    public Edge(int targetNodeId, double weight) {
        this.targetNodeId = targetNodeId;
        this.weight = weight;
    }

    public int getTargetNodeId() {
        return targetNodeId;
    }

    public double getWeight() {
        return weight;
    }
}
