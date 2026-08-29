package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.model.RouteRequest;
import com.agrifleet.route_optimization_service.model.RouteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteResultRepository extends JpaRepository<RouteResult, Long> {

    List<RouteResult> findByStatus(RouteResult.Status status);

    List<RouteResult> findByRequest_Algorithm(RouteRequest.Algorithm algorithm);

    List<RouteResult> findByRequest_Vehicle_Id(Long vehicleId);
}
