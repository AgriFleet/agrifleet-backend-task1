package com.agrifleet.route_optimization_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Immutable
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @Column(name = "specs", nullable = false)
    private String specs;

    @Column(name = "pricing", nullable = false)
    private String pricing;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "current_lat", nullable = false)
    private Double currentLat;

    @Column(name = "current_lng", nullable = false)
    private Double currentLng;

    @Column(name = "availability_status")
    private String availabilityStatus;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
}
