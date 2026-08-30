package com.agrifleet.route_optimization_service.dto;

/**
 * Side-by-side A* vs Dijkstra benchmark result ({@code POST /routes/compare}).
 */
public record CompareResultDto(
        Long startNodeId,
        Long endNodeId,
        AlgorithmMetricsDto aStar,
        AlgorithmMetricsDto dijkstra,
        String conclusion
) {}
