package com.agrifleet.route_optimization_service.algorithm;

import com.agrifleet.route_optimization_service.model.Edge;
import com.agrifleet.route_optimization_service.model.Graph;
import com.agrifleet.route_optimization_service.model.Node;
import com.agrifleet.route_optimization_service.model.RouteResult;

import java.util.*;

public class AStarAlgorithm {

    private static final int EARTH_RADIUS_KM = 6371;

    public RouteResult findShortestPath(Graph graph, int startNodeId, int endNodeId) {
        long startTime = System.currentTimeMillis();

        Node targetNode = graph.getNode(endNodeId);
        if (targetNode == null) {
            return new RouteResult(new ArrayList<>(), -1.0, 0, System.currentTimeMillis() - startTime);
        }

        // setup the priority queue
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        Map<Integer, Double> gScores = new HashMap<>();
        Map<Integer, Integer> previousNodes = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        // set initial scores to max
        for (int nodeId : graph.getAllNodeIds()) {
            gScores.put(nodeId, Double.MAX_VALUE);
        }

        // setup starting node
        gScores.put(startNodeId, 0.0);
        Node startNode = graph.getNode(startNodeId);
        double initialHScore = calculateHaversineDistance(startNode, targetNode);
        pq.offer(new double[]{startNodeId, initialHScore});

        int nodesVisitedCount = 0;

        while (!pq.isEmpty()) {
            double[] current = pq.poll();
            int currentNodeId = (int) current[0];

            // skip if already visited
            if (visited.contains(currentNodeId)) {
                continue;
            }

            visited.add(currentNodeId);
            nodesVisitedCount++;

            // stop if we reached the destination
            if (currentNodeId == endNodeId) {
                break;
            }

            double currentGScore = gScores.get(currentNodeId);
            List<Edge> neighbors = graph.getNeighbors(currentNodeId);

            // check all connected edges
            for (Edge edge : neighbors) {
                int neighborId = edge.getTargetNodeId();
                if (visited.contains(neighborId)) continue;

                double tentativeGScore = currentGScore + edge.getWeight();

                if (tentativeGScore < gScores.get(neighborId)) {
                    previousNodes.put(neighborId, currentNodeId);
                    gScores.put(neighborId, tentativeGScore);

                    Node neighborNode = graph.getNode(neighborId);
                    double fScore = tentativeGScore + calculateHaversineDistance(neighborNode, targetNode);
                    pq.offer(new double[]{neighborId, fScore});
                }
            }
        }

        // return empty if path not found
        if (!gScores.containsKey(endNodeId) || gScores.get(endNodeId) == Double.MAX_VALUE) {
            return new RouteResult(new ArrayList<>(), -1.0, nodesVisitedCount, System.currentTimeMillis() - startTime);
        }

        // build the final path sequence
        List<Integer> path = new ArrayList<>();
        Integer currentId = endNodeId;
        while (currentId != null) {
            path.add(currentId);
            currentId = previousNodes.get(currentId);
        }
        Collections.reverse(path);

        long executionTime = System.currentTimeMillis() - startTime;
        return new RouteResult(path, gScores.get(endNodeId), nodesVisitedCount, executionTime);
    }

    // calculate distance between two nodes
    private double calculateHaversineDistance(Node n1, Node n2) {
        if (n1 == null || n2 == null) return 0.0;

        double dLat = Math.toRadians(n2.getLat() - n1.getLat());
        double dLng = Math.toRadians(n2.getLng() - n1.getLng());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(n1.getLat())) * Math.cos(Math.toRadians(n2.getLat())) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
