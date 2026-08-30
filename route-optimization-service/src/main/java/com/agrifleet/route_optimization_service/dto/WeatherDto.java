package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.WeatherCondition;

import java.time.LocalDateTime;

/**
 * Request/response payload for a weather state attached to a road node
 * ({@code weather_conditions}). For the {@code PUT /weather/{nodeId}} request,
 * {@code nodeId} and {@code updatedAt} are ignored.
 */
public record WeatherDto(
        Long nodeId,
        WeatherCondition.Condition condition,
        Double rainIntensity,
        LocalDateTime updatedAt
) {
    public static WeatherDto from(WeatherCondition w) {
        return new WeatherDto(
                w.getNode() != null ? w.getNode().getId() : null,
                w.getCondition(),
                w.getRainIntensity(),
                w.getUpdatedAt());
    }
}
