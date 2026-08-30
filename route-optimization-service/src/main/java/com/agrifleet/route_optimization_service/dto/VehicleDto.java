package com.agrifleet.route_optimization_service.dto;

import com.agrifleet.route_optimization_service.model.Vehicle;

/**
 * Request/response payload for a piece of machinery ({@code vehicles}).
 * For requests, {@code id} is ignored; for updates, {@code null} fields keep
 * their existing values.
 */
public record VehicleDto(
        Long id,
        String name,
        Vehicle.VehicleType vehicleType,
        Double maxWeightTonnes,
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
