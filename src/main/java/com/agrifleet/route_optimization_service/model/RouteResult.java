package com.agrifleet.route_optimization_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Summary of a computed route (output side of the computation) plus the benchmark
 * metrics used for experimental performance evaluation (Chapter 8).
 * Maps to the {@code route_results} table.
 */
@Entity
@Table(name = "route_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResult {

    public enum Status { FOUND, NOT_FOUND, BLOCKED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private RouteRequest request;

    @Column(name = "total_distance_km", nullable = false)
    @Builder.Default
    private Double totalDistanceKm = 0.0;

    @Column(name = "total_travel_time_min", nullable = false)
    @Builder.Default
    private Double totalTravelTimeMin = 0.0;

    /** Number of vertices popped from the heap - used as a benchmark metric. */
    @Column(name = "node_expansions", nullable = false)
    @Builder.Default
    private Integer nodeExpansions = 0;

    /** Wall-clock execution time in ms - benchmark metric for Chapter 8. */
    @Column(name = "execution_time_ms", nullable = false)
    @Builder.Default
    private Double executionTimeMs = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Ordered GPS path vectors (cascade-saved together with the result). */
    @OneToMany(mappedBy = "routeResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoutePathSegment> pathSegments = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (nodeExpansions == null) nodeExpansions = 0;
        if (executionTimeMs == null) executionTimeMs = 0.0;
    }
}
