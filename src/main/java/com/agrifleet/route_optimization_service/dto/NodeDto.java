package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.RoadNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request/response payload for a road network vertex ({@code road_nodes}).
 * For requests, {@code id} and {@code createdAt} are ignored; for updates,
 * {@code null} fields keep their existing values.
 */
public record NodeDto(
        Long id,
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "latitude is required") Double latitude,
        @NotNull(message = "longitude is required") Double longitude,
        @NotNull(message = "nodeType is required") RoadNode.NodeType nodeType,
        String farmId,
        Boolean isActive,
        LocalDateTime createdAt
) {
    public static NodeDto from(RoadNode n) {
        return new NodeDto(
                n.getId(),
                n.getName(),
                n.getLatitude(),
                n.getLongitude(),
                n.getNodeType(),
                n.getFarmId(),
                n.getIsActive(),
                n.getCreatedAt());
    }
}
