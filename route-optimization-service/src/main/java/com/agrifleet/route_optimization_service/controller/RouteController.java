package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.CompareResultDto;
import com.agrifleet.route_optimization_service.dto.OptimizeRouteRequest;
import com.agrifleet.route_optimization_service.dto.PathTracingResponse;
import com.agrifleet.route_optimization_service.dto.RouteResponse;
import com.agrifleet.route_optimization_service.dto.RouteSummaryDto;
import com.agrifleet.route_optimization_service.model.RouteRequest;
import com.agrifleet.route_optimization_service.model.RouteResult;
import com.agrifleet.route_optimization_service.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for the core routing engine ({@code /api/v1/routes}).
 */
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    /** Core endpoint - computes the optimal route (A* or Dijkstra). */
    @PostMapping("/optimize")
    public RouteResponse optimize(@RequestBody OptimizeRouteRequest request) {
        return routeService.optimize(request);
    }

    /** List past route results (filters: vehicleId, status, algorithm). */
    @GetMapping
    public List<RouteSummaryDto> list(@RequestParam(required = false) Long vehicleId,
                                      @RequestParam(required = false) RouteResult.Status status,
                                      @RequestParam(required = false) RouteRequest.Algorithm algorithm) {
        return routeService.listRoutes(vehicleId, status, algorithm);
    }

    /** One route summary. */
    @GetMapping("/{resultId}")
    public RouteSummaryDto get(@PathVariable Long resultId) {
        return routeService.getRoute(resultId);
    }

    /** Exact coordinate-level GPS path vectors for live tracking / map drawing. */
    @GetMapping("/{resultId}/path")
    public PathTracingResponse getPath(@PathVariable Long resultId) {
        return routeService.getPath(resultId);
    }

    /** A* vs Dijkstra benchmark on the same query pair. */
    @PostMapping("/compare")
    public CompareResultDto compare(@RequestBody OptimizeRouteRequest request) {
        return routeService.compare(request);
    }
}
