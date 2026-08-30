package com.agrifleet.route_optimization_service.service.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory directed graph (adjacency list) handed to A* / Dijkstra.
 * Built by {@code RouteService} from the persisted road network, with
 * weather/surface costs and bridge pruning already applied.
 */
public class RoutingGraph {

    private final Map<Long, List<GraphEdge>> adjacency = new HashMap<>();
    private final Map<Long, NodeCoord> coords = new HashMap<>();
    private double maxSpeedKmh = 0.0;

    public void addNode(long id, double latitude, double longitude) {
        coords.put(id, new NodeCoord(latitude, longitude));
        adjacency.computeIfAbsent(id, k -> new ArrayList<>());
    }

    public void addEdge(long from, long to, double costMinutes, double distanceKm, long roadEdgeId) {
        adjacency.computeIfAbsent(from, k -> new ArrayList<>())
                .add(new GraphEdge(to, costMinutes, distanceKm, roadEdgeId));
    }

    public void setMaxSpeedKmh(double maxSpeedKmh) {
        this.maxSpeedKmh = maxSpeedKmh;
    }

    public Map<Long, List<GraphEdge>> adjacency() {
        return adjacency;
    }

    public Map<Long, NodeCoord> coords() {
        return coords;
    }

    public double maxSpeedKmh() {
        return maxSpeedKmh;
    }

    public int vertexCount() {
        return coords.size();
    }
}
