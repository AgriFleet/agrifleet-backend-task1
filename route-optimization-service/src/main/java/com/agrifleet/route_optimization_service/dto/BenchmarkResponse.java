package com.agrifleet.route_optimization_service.dto;

import java.util.List;

/**
 * Benchmark response ({@code POST /benchmark}).
 */
public record BenchmarkResponse(
        List<BenchmarkResultDto> results,
        String expectedCurve
) {}
