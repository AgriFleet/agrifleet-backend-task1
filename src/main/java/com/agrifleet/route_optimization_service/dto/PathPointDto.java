package com.agrifleet.route_optimization_service.dto;

/**
 * One turn-by-turn waypoint of a computed route (rendered as a map polyline).
 */
public record PathPointDto(
        Long nodeId,
        String name,
        Double lat,
        Double lng,
        Double km,
        Double min
) {}
