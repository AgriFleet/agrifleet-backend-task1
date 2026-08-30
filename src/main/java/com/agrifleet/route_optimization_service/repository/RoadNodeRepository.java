package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.model.RoadNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadNodeRepository extends JpaRepository<RoadNode, Long> {

    List<RoadNode> findByNodeType(RoadNode.NodeType nodeType);

    List<RoadNode> findByIsActiveTrue();

    List<RoadNode> findByIsActive(Boolean isActive);

    List<RoadNode> findByNameContainingIgnoreCase(String name);

    long countByNodeType(RoadNode.NodeType nodeType);
}
