package com.agrifleet.route_optimization_service.dto;

/**
 * One point on the benchmark scalability curve.
 */
public record BenchmarkResultDto(
        int vertices,
        double avgExecutionTimeMs,
        long avgNodeExpansions
) {}
