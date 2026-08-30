package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RouteResult;

import java.util.List;

/**
 * Full response of {@code POST /routes/optimize}.
 */
public record RouteResponse(
        Long routeId,
        String algorithm,
        RouteResult.Status status,
        Double totalDistanceKm,
        Double totalTravelTimeMin,
        Integer nodeExpansions,
        Double executionTimeMs,
        List<PathPointDto> path,
        List<String> warnings
) {}
