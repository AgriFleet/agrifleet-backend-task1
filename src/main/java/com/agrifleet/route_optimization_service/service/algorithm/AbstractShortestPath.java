package com.agrifleet.route_optimization_service.service.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Shared informed/uninformed search loop for A* and Dijkstra.
 *
 * <p>Both algorithms maintain a min-heap open set, a closed set, a g-score map,
 * and a predecessor map (path backtracking). The only difference is the heuristic:
 * A* uses the admissible Haversine lower bound, Dijkstra uses h(n) = 0.
 *
 * <p>Data structures (per the coursework spec):
 * adjacency list (graph), min binary heap (priority queue),
 * hash set (closed set), hash map (predecessors / g-scores).
 */
public abstract class AbstractShortestPath implements ShortestPathAlgorithm {

    /** Heap entry: node id + f-score (g + h). */
    private record Entry(long node, double f) {
    }

    @Override
    public SearchResult findShortestPath(RoutingGraph graph, long start, long target) {
        Map<Long, List<GraphEdge>> adjacency = graph.adjacency();
        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();
        Map<Long, GraphEdge> edgeUsed = new HashMap<>();
        Set<Long> closed = new HashSet<>();

        PriorityQueue<Entry> open = new PriorityQueue<>(Comparator.comparingDouble(Entry::f));
        gScore.put(start, 0.0);
        open.add(new Entry(start, heuristic(graph, start, target)));
        int expansions = 0;

        while (!open.isEmpty()) {
            long current = open.poll().node();
            if (current == target) {
                return reconstruct(cameFrom, edgeUsed, start, target,
                        gScore.getOrDefault(target, 0.0), expansions);
            }
            if (!closed.add(current)) {
                continue; // stale heap entry for an already-expanded node
            }
            expansions++;

            for (GraphEdge edge : adjacency.getOrDefault(current, List.of())) {
                if (closed.contains(edge.to())) {
                    continue;
                }
                double tentativeG = gScore.get(current) + edge.costMinutes();
                if (tentativeG < gScore.getOrDefault(edge.to(), Double.POSITIVE_INFINITY)) {
                    cameFrom.put(edge.to(), current);
                    edgeUsed.put(edge.to(), edge);
                    gScore.put(edge.to(), tentativeG);
                    open.add(new Entry(edge.to(), tentativeG + heuristic(graph, edge.to(), target)));
                }
            }
        }
        return SearchResult.notFound(expansions);
    }

    /**
     * Heuristic h(n): must be admissible (never overestimate) for A* optimality.
     * Dijkstra returns 0 for every node.
     */
    protected abstract double heuristic(RoutingGraph graph, long node, long target);

    private SearchResult reconstruct(Map<Long, Long> cameFrom, Map<Long, GraphEdge> edgeUsed,
                                     long start, long target, double totalCost, int expansions) {
        List<Long> path = new ArrayList<>();
        List<GraphEdge> edges = new ArrayList<>();
        long current = target;
        while (current != start) {
            path.add(current);
            GraphEdge edge = edgeUsed.get(current);
            if (edge == null) {
                break;
            }
            edges.add(edge);
            current = cameFrom.getOrDefault(current, start);
        }
        path.add(start);
        Collections.reverse(path);
        Collections.reverse(edges);
        double totalDistance = edges.stream().mapToDouble(GraphEdge::distanceKm).sum();
        return new SearchResult(true, path, edges, totalCost, totalDistance, expansions);
    }
}
