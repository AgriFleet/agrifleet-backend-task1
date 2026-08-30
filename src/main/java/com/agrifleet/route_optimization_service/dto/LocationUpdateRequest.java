package com.agrifleet.route_optimization_service.dto;

/**
 * Payload for live GPS path tracing ({@code PUT /vehicles/{id}/location}).
 * All fields are optional; only provided fields are updated.
 */
public record LocationUpdateRequest(
        Double latitude,
        Double longitude,
        Long currentNodeId
) {}
