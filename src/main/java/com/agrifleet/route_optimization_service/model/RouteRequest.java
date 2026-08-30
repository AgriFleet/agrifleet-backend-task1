package com.agrifleet.route_optimization_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Logs an incoming route-optimization request (input side of the computation).
 * Maps to the {@code route_requests} table.
 */
@Entity
@Table(name = "route_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRequest {

    public enum Algorithm { ASTAR, DIJKSTRA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "start_node_id", nullable = false)
    private RoadNode startNode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "end_node_id", nullable = false)
    private RoadNode endNode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Algorithm algorithm = Algorithm.ASTAR;

    /** 1 = apply rain penalties to unpaved edges. */
    @Column(name = "weather_aware", nullable = false)
    @Builder.Default
    private Boolean weatherAware = false;

    /** Gross vehicle weight (tonnes) used for bridge tolerance checks. */
    @Column(name = "weight_unit_tonnes", nullable = false)
    @Builder.Default
    private Double weightUnitTonnes = 0.0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (algorithm == null) algorithm = Algorithm.ASTAR;
        if (weatherAware == null) weatherAware = false;
        if (weightUnitTonnes == null) weightUnitTonnes = 0.0;
    }
}
