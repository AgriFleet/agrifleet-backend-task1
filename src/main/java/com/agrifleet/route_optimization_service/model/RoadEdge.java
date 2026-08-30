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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Directed road segment between two {@link RoadNode}s. This is the payload of the
 * adjacency-list graph used by the A* / Dijkstra routing engine.
 * Maps to the {@code road_edges} table.
 */
@Entity
@Table(name = "road_edges",
       uniqueConstraints = @UniqueConstraint(columnNames = {"source_node_id", "target_node_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadEdge {

    public enum SurfaceType { PAVED, GRAVEL, MUD, DIRT_TRACK, BRIDGE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_node_id", nullable = false)
    private RoadNode sourceNode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_node_id", nullable = false)
    private RoadNode targetNode;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "surface_type", nullable = false)
    private SurfaceType surfaceType;

    @Column(name = "speed_limit_kmh", nullable = false)
    private Double speedLimitKmh;

    /** Only populated for BRIDGE surfaces; enforces the machinery weight restriction. */
    @Column(name = "weight_limit_tonnes")
    private Double weightLimitTonnes;

    /** 1 = traversable in both directions (reverse edge added by the graph builder). */
    @Column(name = "is_bidirectional", nullable = false)
    @Builder.Default
    private Boolean isBidirectional = true;

    /** 1 = open for traffic, 0 = closed/under maintenance. */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
