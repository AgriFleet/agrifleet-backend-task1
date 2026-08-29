package com.agrifleet.route_optimization_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "road_edges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edge_id")
    private Long edgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_node", nullable = false)
    private RoadNode uNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "v_node", nullable = false)
    private RoadNode vNode;

    @Column(name = "base_distance_km", nullable = false)
    private Double baseDistanceKm;

    @Column(name = "surface_type")
    private String surfaceType;

    @Column(name = "max_weight_tonnes")
    private Double maxWeightTonnes;

    @Column(name = "weather_penalty_multiplier")
    private Double weatherPenaltyMultiplier;

    @Column(name = "computed_weight", nullable = false)
    private Double computedWeight;
}
