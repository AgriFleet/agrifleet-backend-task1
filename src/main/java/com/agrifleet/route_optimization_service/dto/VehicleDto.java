package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request/response payload for a piece of machinery ({@code vehicles}).
 * For requests, {@code id} is ignored; for updates, {@code null} fields keep
 * their existing values.
 */
public record VehicleDto(
        Long id,
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "vehicleType is required") Vehicle.VehicleType vehicleType,
        @NotNull(message = "maxWeightTonnes is required")
        @Positive(message = "maxWeightTonnes must be greater than 0") Double maxWeightTonnes,
        Long currentNodeId,
        Double currentLatitude,
        Double currentLongitude,
        Double speedFactor,
        Boolean isAvailable
) {
    public static VehicleDto from(Vehicle v) {
        return new VehicleDto(
                v.getId(),
                v.getName(),
                v.getVehicleType(),
                v.getMaxWeightTonnes(),
                v.getCurrentNode() != null ? v.getCurrentNode().getId() : null,
                v.getCurrentLatitude(),
                v.getCurrentLongitude(),
                v.getSpeedFactor(),
                v.getIsAvailable());
    }
}
