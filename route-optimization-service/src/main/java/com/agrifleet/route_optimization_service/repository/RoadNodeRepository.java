package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.entity.RoadNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadNodeRepository extends JpaRepository<RoadNode, Long> {
}
