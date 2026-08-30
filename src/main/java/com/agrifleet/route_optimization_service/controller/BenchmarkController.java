package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.BenchmarkRequest;
import com.agrifleet.route_optimization_service.dto.BenchmarkResponse;
import com.agrifleet.route_optimization_service.service.BenchmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Experimental performance benchmarking ({@code POST /api/v1/benchmark}) -
 * generates the execution-time-vs-N curve for the report's Chapter 8.
 */
@RestController
@RequestMapping("/api/v1/benchmark")
@RequiredArgsConstructor
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    @PostMapping
    public BenchmarkResponse run(@RequestBody BenchmarkRequest request) {
        return benchmarkService.run(request);
    }
}
