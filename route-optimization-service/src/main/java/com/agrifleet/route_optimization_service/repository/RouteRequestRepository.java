package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.model.RouteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRequestRepository extends JpaRepository<RouteRequest, Long> {

    List<RouteRequest> findByVehicleId(Long vehicleId);

    List<RouteRequest> findByAlgorithm(RouteRequest.Algorithm algorithm);
}
