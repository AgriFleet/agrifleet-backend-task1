package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RoadEdge;

/**
 * Request/response payload for a directed road segment ({@code road_edges}).
 * For requests, {@code id} is ignored; for updates, {@code null} fields keep
 * their existing values.
 */
public record EdgeDto(
        Long id,
        Long sourceNodeId,
        Long targetNodeId,
        Double distanceKm,
        RoadEdge.SurfaceType surfaceType,
        Double speedLimitKmh,
        Double weightLimitTonnes,
        Boolean isBidirectional,
        Boolean isActive
) {
    public static EdgeDto from(RoadEdge e) {
        return new EdgeDto(
                e.getId(),
                e.getSourceNode() != null ? e.getSourceNode().getId() : null,
                e.getTargetNode() != null ? e.getTargetNode().getId() : null,
                e.getDistanceKm(),
                e.getSurfaceType(),
                e.getSpeedLimitKmh(),
                e.getWeightLimitTonnes(),
                e.getIsBidirectional(),
                e.getIsActive());
    }
}
