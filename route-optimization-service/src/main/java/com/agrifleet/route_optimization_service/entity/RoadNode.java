package com.agrifleet.route_optimization_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "road_nodes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadNode {

    @Id
    @Column(name = "node_id")
    private Long nodeId;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "elevation_meters")
    private Double elevationMeters;

    @Column(name = "is_farm_gate")
    private Integer isFarmGate;

    @Column(name = "is_depot")
    private Integer isDepot;
}
