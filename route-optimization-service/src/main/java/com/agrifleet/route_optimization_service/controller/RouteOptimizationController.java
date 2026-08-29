package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.model.RouteResult;
import com.agrifleet.route_optimization_service.service.AStarService;
import com.agrifleet.route_optimization_service.service.DijkstraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteOptimizationController {

    private final DijkstraService dijkstraService;
    private final AStarService aStarService;

    public RouteOptimizationController(DijkstraService dijkstraService, AStarService aStarService) {
        this.dijkstraService = dijkstraService;
        this.aStarService = aStarService;
    }

    @GetMapping("/compare")
    public Map<String, RouteResult> compareAlgorithms(@RequestParam int startNode, @RequestParam int endNode) {
        // get results from services
        RouteResult dijkstraResult = dijkstraService.executeOptimization(startNode, endNode);
        RouteResult aStarResult = aStarService.executeOptimization(startNode, endNode);

        // format output
        Map<String, RouteResult> comparison = new HashMap<>();
        comparison.put("Dijkstra", dijkstraResult);
        comparison.put("AStar", aStarResult);

        return comparison;
    }
}
