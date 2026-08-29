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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Current weather state attached to a road node/region. Drives the weather-aware
 * cost penalty applied to unpaved edges during A* / Dijkstra routing.
 * Maps to the {@code weather_conditions} table (one row per node).
 */
@Entity
@Table(name = "weather_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherCondition {

    public enum Condition { CLEAR, LIGHT_RAIN, HEAVY_RAIN, FLOOD }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "node_id", nullable = false, unique = true)
    private RoadNode node;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Condition condition = Condition.CLEAR;

    /** Rain intensity 0.0 (dry) .. 1.0 (downpour) - drives the unpaved-edge penalty. */
    @Column(name = "rain_intensity", nullable = false)
    @Builder.Default
    private Double rainIntensity = 0.0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = LocalDateTime.now();
        if (condition == null) condition = Condition.CLEAR;
        if (rainIntensity == null) rainIntensity = 0.0;
    }
}
