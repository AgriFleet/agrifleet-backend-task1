package com.agrifleet.route_optimization_service.service.algorithm;

import org.springframework.stereotype.Component;

/**
 * Task 1 <b>baseline</b> algorithm: Dijkstra (uniform-cost search).
 *
 * <p>Identical search loop to A* but with h(n) = 0, so it expands vertices
 * radially in all directions. Used as the comparison baseline by
 * {@code POST /routes/compare}.
 *
 * <p>Complexity: O((V + E) log V) with a min binary heap.
 */
@Component
public class DijkstraAlgorithm extends AbstractShortestPath {

    @Override
    protected double heuristic(RoutingGraph graph, long node, long target) {
        return 0.0; // uninformed search
    }
}
