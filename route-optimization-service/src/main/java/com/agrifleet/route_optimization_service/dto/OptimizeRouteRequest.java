package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RouteRequest;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for {@code POST /routes/optimize} and {@code POST /routes/compare}.
 */
public record OptimizeRouteRequest(
        Long vehicleId,
        @NotNull(message = "startNodeId is required") Long startNodeId,
        @NotNull(message = "endNodeId is required") Long endNodeId,
        RouteRequest.Algorithm algorithm,
        Boolean weatherAware,
        Double weightUnitTonnes
) {}
