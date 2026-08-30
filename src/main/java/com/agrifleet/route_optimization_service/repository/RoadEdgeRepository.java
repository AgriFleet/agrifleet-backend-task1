package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.model.RoadEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoadEdgeRepository extends JpaRepository<RoadEdge, Long> {

    List<RoadEdge> findBySourceNodeId(Long sourceNodeId);

    List<RoadEdge> findBySourceNodeIdAndIsActiveTrue(Long sourceNodeId);

    List<RoadEdge> findBySourceNodeIdOrTargetNodeId(Long sourceNodeId, Long targetNodeId);

    List<RoadEdge> findBySurfaceType(RoadEdge.SurfaceType surfaceType);

    List<RoadEdge> findByIsActiveTrue();

    Optional<RoadEdge> findBySourceNodeIdAndTargetNodeId(Long sourceNodeId, Long targetNodeId);

    boolean existsBySourceNodeIdAndTargetNodeId(Long sourceNodeId, Long targetNodeId);

    long countBySurfaceType(RoadEdge.SurfaceType surfaceType);
}
