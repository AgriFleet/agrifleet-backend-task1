package com.agrifleet.route_optimization_service.algorithm;

import com.agrifleet.route_optimization_service.model.Edge;
import com.agrifleet.route_optimization_service.model.Graph;
import com.agrifleet.route_optimization_service.model.RouteResult;

import java.util.*;

public class DijkstraAlgorithm {

    public RouteResult findShortestPath(Graph graph, int startNodeId, int endNodeId) {
        long startTime = System.currentTimeMillis();

        // setup the priority queue
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previousNodes = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        // set initial distances to max
        for (int nodeId : graph.getAllNodeIds()) {
            distances.put(nodeId, Double.MAX_VALUE);
        }

        // setup starting node
        distances.put(startNodeId, 0.0);
        pq.offer(new double[]{startNodeId, 0.0});

        int nodesVisitedCount = 0;

        while (!pq.isEmpty()) {
            double[] current = pq.poll();
            int currentNodeId = (int) current[0];
            double currentDistance = current[1];

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

            // check all connected edges
            List<Edge> neighbors = graph.getNeighbors(currentNodeId);
            for (Edge edge : neighbors) {
                int neighborId = edge.getTargetNodeId();
                if (visited.contains(neighborId)) continue;

                double newDistance = currentDistance + edge.getWeight();
                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    previousNodes.put(neighborId, currentNodeId);
                    pq.offer(new double[]{neighborId, newDistance});
                }
            }
        }

        // return empty if path not found
        if (!distances.containsKey(endNodeId) || distances.get(endNodeId) == Double.MAX_VALUE) {
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
        return new RouteResult(path, distances.get(endNodeId), nodesVisitedCount, executionTime);
    }
}
