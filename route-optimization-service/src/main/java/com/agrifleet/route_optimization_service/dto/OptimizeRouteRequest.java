package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RouteRequest;

/**
 * Request payload for {@code POST /routes/optimize} and {@code POST /routes/compare}.
 */
public record OptimizeRouteRequest(
        Long vehicleId,
        Long startNodeId,
        Long endNodeId,
        RouteRequest.Algorithm algorithm,
        Boolean weatherAware,
        Double weightUnitTonnes
) {}
