package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.LocationUpdateRequest;
import com.agrifleet.route_optimization_service.dto.VehicleDto;
import com.agrifleet.route_optimization_service.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for the machinery fleet and live GPS tracking ({@code /api/v1/vehicles}).
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleDto create(@Valid @RequestBody VehicleDto dto) {
        return vehicleService.createVehicle(dto);
    }

    @GetMapping
    public List<VehicleDto> list(@RequestParam(required = false) Boolean available) {
        return vehicleService.listVehicles(available);
    }

    @GetMapping("/{id}")
    public VehicleDto get(@PathVariable Long id) {
        return vehicleService.getVehicle(id);
    }

    @PutMapping("/{id}")
    public VehicleDto update(@PathVariable Long id, @RequestBody VehicleDto dto) {
        return vehicleService.updateVehicle(id, dto);
    }

    /** Live GPS path tracing endpoint. */
    @PutMapping("/{id}/location")
    public VehicleDto updateLocation(@PathVariable Long id, @RequestBody LocationUpdateRequest location) {
        return vehicleService.updateLocation(id, location);
    }
}
