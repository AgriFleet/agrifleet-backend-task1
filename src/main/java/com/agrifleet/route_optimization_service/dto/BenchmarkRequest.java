package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RouteRequest;

import java.util.List;

/**
 * Request payload for {@code POST /benchmark} - generates the Chapter 8
 * execution-time-vs-N scalability curve.
 */
public record BenchmarkRequest(
        List<Integer> sizes,
        RouteRequest.Algorithm algorithm,
        Integer runsPerSize
) {}
