package com.agrifleet.route_optimization_service.dto;

/**
 * One stored GPS path-tracing point ({@code GET /routes/{resultId}/path}).
 */
public record TracePointDto(
        Integer sequence,
        Long nodeId,
        Double lat,
        Double lng,
        Double distanceFromStartKm,
        Double cumulativeTimeMin
) {}
