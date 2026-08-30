package com.agrifleet.route_optimization_service.dto;

/**
 * Result of a bulk graph import ({@code POST /graph/import}).
 */
public record ImportResultDto(
        int nodesImported,
        int edgesImported,
        long nodesTotal,
        long edgesTotal
) {}
