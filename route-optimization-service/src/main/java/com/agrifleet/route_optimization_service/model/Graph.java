package com.agrifleet.route_optimization_service.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacencyList.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addEdge(int sourceNodeId, int targetNodeId, double weight) {
        adjacencyList.computeIfAbsent(sourceNodeId, k -> new ArrayList<>())
                     .add(new Edge(targetNodeId, weight));
    }

    public Node getNode(int nodeId) {
        return nodes.get(nodeId);
    }

    public List<Edge> getNeighbors(int nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }

    public Set<Integer> getAllNodeIds() {
        return nodes.keySet();
    }
}
