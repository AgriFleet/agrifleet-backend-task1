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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A piece of machinery in the fleet (combine harvester / tractor / truck) tracked
 * by the route optimization service. Provides the gross weight used for bridge
 * tolerance checks and the speed factor used in route costing.
 * Maps to the {@code vehicles} table.
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    public enum VehicleType { COMBINE_HARVESTER, TRACTOR, TRUCK }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "max_weight_tonnes", nullable = false)
    private Double maxWeightTonnes;

    /** Node the machine is currently parked at / last reported on. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_node_id")
    private RoadNode currentNode;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    /** 0.5 (mud tyres) .. 1.2 (fast truck); scales travel-time cost. */
    @Column(name = "speed_factor", nullable = false)
    @Builder.Default
    private Double speedFactor = 1.0;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;
}
