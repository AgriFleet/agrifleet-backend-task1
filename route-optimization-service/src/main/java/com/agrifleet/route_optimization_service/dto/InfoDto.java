package com.agrifleet.route_optimization_service.dto;

import java.util.List;

/**
 * Service metadata payload ({@code GET /api/v1/info}).
 */
public record InfoDto(
        String service,
        String version,
        List<String> algorithms,
        String heuristic,
        boolean weatherAware,
        String status
) {}
