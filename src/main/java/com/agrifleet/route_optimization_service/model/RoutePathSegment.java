package com.agrifleet.route_optimization_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * One coordinate-level point of a computed route, ordered by {@code sequence}.
 * Enables live vehicle path tracing and map polyline rendering.
 * Maps to the {@code route_path_segments} table.
 */
@Entity
@Table(name = "route_path_segments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"route_result_id", "sequence"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePathSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_result_id", nullable = false)
    private RouteResult routeResult;

    /** 0 = start, N = destination. */
    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id")
    private RoadNode node;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "distance_from_start_km", nullable = false)
    @Builder.Default
    private Double distanceFromStartKm = 0.0;

    @Column(name = "cumulative_time_min", nullable = false)
    @Builder.Default
    private Double cumulativeTimeMin = 0.0;
}
