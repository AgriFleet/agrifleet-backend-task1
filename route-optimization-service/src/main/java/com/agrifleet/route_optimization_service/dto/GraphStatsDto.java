package com.agrifleet.route_optimization_service.dto;

/**
 * Graph statistics payload ({@code GET /graph/stats}).
 */
public record GraphStatsDto(
        long vertices,
        long edges,
        long depots,
        long farms,
        long bridges,
        double pavedKm,
        double unpavedKm
) {}
