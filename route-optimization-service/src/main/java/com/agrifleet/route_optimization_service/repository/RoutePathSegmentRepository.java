package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.model.RoutePathSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutePathSegmentRepository extends JpaRepository<RoutePathSegment, Long> {

    List<RoutePathSegment> findByRouteResultIdOrderBySequenceAsc(Long routeResultId);

    void deleteByRouteResultId(Long routeResultId);
}
