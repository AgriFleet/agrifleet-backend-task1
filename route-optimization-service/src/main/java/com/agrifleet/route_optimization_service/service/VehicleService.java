package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.dto.LocationUpdateRequest;
import com.agrifleet.route_optimization_service.dto.VehicleDto;
import com.agrifleet.route_optimization_service.exception.BadRequestException;
import com.agrifleet.route_optimization_service.exception.ResourceNotFoundException;
import com.agrifleet.route_optimization_service.model.RoadNode;
import com.agrifleet.route_optimization_service.model.Vehicle;
import com.agrifleet.route_optimization_service.repository.RoadNodeRepository;
import com.agrifleet.route_optimization_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manages the machinery fleet and its live GPS positions (path tracing input).
 */
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RoadNodeRepository nodeRepository;

    public VehicleDto createVehicle(VehicleDto dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BadRequestException("name is required");
        }
        if (dto.vehicleType() == null) {
            throw new BadRequestException("vehicleType is required");
        }
        if (dto.maxWeightTonnes() == null || dto.maxWeightTonnes() <= 0) {
            throw new BadRequestException("maxWeightTonnes must be greater than 0");
        }

        RoadNode node = dto.currentNodeId() != null ? findNode(dto.currentNodeId()) : null;
        Vehicle vehicle = Vehicle.builder()
                .name(dto.name().trim())
                .vehicleType(dto.vehicleType())
                .maxWeightTonnes(dto.maxWeightTonnes())
                .currentNode(node)
                .currentLatitude(dto.currentLatitude() != null
                        ? dto.currentLatitude()
                        : (node != null ? node.getLatitude() : null))
                .currentLongitude(dto.currentLongitude() != null
                        ? dto.currentLongitude()
                        : (node != null ? node.getLongitude() : null))
                .speedFactor(dto.speedFactor() != null ? dto.speedFactor() : 1.0)
                .isAvailable(dto.isAvailable() != null ? dto.isAvailable() : true)
                .build();
        return VehicleDto.from(vehicleRepository.save(vehicle));
    }

    public VehicleDto getVehicle(Long id) {
        return VehicleDto.from(findVehicle(id));
    }

    public List<VehicleDto> listVehicles(Boolean available) {
        List<Vehicle> vehicles = Boolean.TRUE.equals(available)
                ? vehicleRepository.findByIsAvailableTrue()
                : vehicleRepository.findAll();
        return vehicles.stream().map(VehicleDto::from).toList();
    }

    public VehicleDto updateVehicle(Long id, VehicleDto dto) {
        Vehicle vehicle = findVehicle(id);
        if (dto.name() != null && !dto.name().isBlank()) vehicle.setName(dto.name().trim());
        if (dto.vehicleType() != null) vehicle.setVehicleType(dto.vehicleType());
        if (dto.maxWeightTonnes() != null && dto.maxWeightTonnes() > 0) {
            vehicle.setMaxWeightTonnes(dto.maxWeightTonnes());
        }
        if (dto.currentNodeId() != null) vehicle.setCurrentNode(findNode(dto.currentNodeId()));
        if (dto.currentLatitude() != null) vehicle.setCurrentLatitude(dto.currentLatitude());
        if (dto.currentLongitude() != null) vehicle.setCurrentLongitude(dto.currentLongitude());
        if (dto.speedFactor() != null) vehicle.setSpeedFactor(dto.speedFactor());
        if (dto.isAvailable() != null) vehicle.setIsAvailable(dto.isAvailable());
        return VehicleDto.from(vehicleRepository.save(vehicle));
    }

    /** Live GPS path tracing - updates only the fields provided in the payload. */
    public VehicleDto updateLocation(Long id, LocationUpdateRequest location) {
        Vehicle vehicle = findVehicle(id);
        if (location.currentNodeId() != null) {
            vehicle.setCurrentNode(findNode(location.currentNodeId()));
        }
        if (location.latitude() != null) vehicle.setCurrentLatitude(location.latitude());
        if (location.longitude() != null) vehicle.setCurrentLongitude(location.longitude());
        return VehicleDto.from(vehicleRepository.save(vehicle));
    }

    private Vehicle findVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle with id " + id + " does not exist"));
    }

    private RoadNode findNode(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Road node with id " + id + " does not exist"));
    }
}
