package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RoadEdge;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request/response payload for a directed road segment ({@code road_edges}).
 * For requests, {@code id} is ignored; for updates, {@code null} fields keep
 * their existing values.
 */
public record EdgeDto(
        Long id,
        @NotNull(message = "sourceNodeId is required") Long sourceNodeId,
        @NotNull(message = "targetNodeId is required") Long targetNodeId,
        @NotNull(message = "distanceKm is required")
        @Positive(message = "distanceKm must be greater than 0") Double distanceKm,
        @NotNull(message = "surfaceType is required") RoadEdge.SurfaceType surfaceType,
        @NotNull(message = "speedLimitKmh is required")
        @Positive(message = "speedLimitKmh must be greater than 0") Double speedLimitKmh,
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
