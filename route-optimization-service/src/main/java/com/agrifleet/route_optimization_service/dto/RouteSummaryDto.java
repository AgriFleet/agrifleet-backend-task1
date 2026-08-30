package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RouteResult;

/**
 * Lightweight route summary (no full path) used by {@code GET /routes}
 * and {@code GET /routes/{resultId}}.
 */
public record RouteSummaryDto(
        Long routeId,
        Long requestId,
        String algorithm,
        RouteResult.Status status,
        Double totalDistanceKm,
        Double totalTravelTimeMin,
        Integer nodeExpansions,
        Double executionTimeMs,
        String createdAt
) {}
