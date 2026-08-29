package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.entity.RoadEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadEdgeRepository extends JpaRepository<RoadEdge, Long> {
}
