package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RouteResult;

import java.util.List;

/**
 * GPS path-tracing response ({@code GET /routes/{resultId}/path}) - the exact
 * coordinate-level path vectors used for live vehicle tracking / map drawing.
 */
public record PathTracingResponse(
        Long routeId,
        RouteResult.Status status,
        Double totalDistanceKm,
        List<TracePointDto> points
) {}
