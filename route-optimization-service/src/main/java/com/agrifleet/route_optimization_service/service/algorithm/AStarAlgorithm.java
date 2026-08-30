package com.agrifleet.route_optimization_service.service.algorithm;

import org.springframework.stereotype.Component;

/**
 * Task 1 <b>selected</b> algorithm: A* Search.
 *
 * <p>f(n) = g(n) + h(n) with the admissible Haversine lower bound
 * h(n) = great-circle distance to target / max effective speed x 60 min.
 * Because h is admissible, A* returns the same optimal path as Dijkstra
 * while expanding substantially fewer vertices (heuristic-directed search).
 *
 * <p>Complexity: average O(E log V); search space is directed toward the target.
 */
@Component
public class AStarAlgorithm extends AbstractShortestPath {

    @Override
    protected double heuristic(RoutingGraph graph, long node, long target) {
        if (node == target) {
            return 0.0;
        }
        NodeCoord a = graph.coords().get(node);
        NodeCoord b = graph.coords().get(target);
        if (a == null || b == null) {
            return 0.0;
        }
        double km = Haversine.distanceKm(a.latitude(), a.longitude(), b.latitude(), b.longitude());
        double maxSpeed = graph.maxSpeedKmh();
        return maxSpeed > 0 ? (km / maxSpeed) * 60.0 : 0.0;
    }
}
