package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByIsAvailableTrue();

    List<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType);
}
