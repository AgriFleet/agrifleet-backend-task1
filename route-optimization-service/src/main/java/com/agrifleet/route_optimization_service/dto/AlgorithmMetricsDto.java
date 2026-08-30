package com.agrifleet.route_optimization_service.dto;

/**
 * Per-algorithm metrics within a {@code /routes/compare} result.
 */
public record AlgorithmMetricsDto(
        boolean found,
        Double distanceKm,
        Double timeMin,
        Integer nodeExpansions,
        Double executionTimeMs,
        Double searchSpacePct
) {}
