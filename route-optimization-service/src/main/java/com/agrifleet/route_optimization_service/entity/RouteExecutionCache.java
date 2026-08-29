package com.agrifleet.route_optimization_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "route_execution_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteExecutionCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(name = "origin_node", nullable = false)
    private Long originNode;

    @Column(name = "destination_node", nullable = false)
    private Long destinationNode;

    @Column(name = "path_node_sequence", nullable = false)
    private String pathNodeSequence;

    @Column(name = "total_distance_km", nullable = false)
    private Double totalDistanceKm;

    @Column(name = "total_travel_time_mins", nullable = false)
    private Double totalTravelTimeMins;

    @Column(name = "nodes_visited_count", nullable = false)
    private Integer nodesVisitedCount;

    @Column(name = "algorithm_used", nullable = false)
    private String algorithmUsed;

    @Column(name = "computed_at")
    private String computedAt;
}
