package com.agrifleet.route_optimization_service.config;

import com.agrifleet.route_optimization_service.model.RoadEdge;
import com.agrifleet.route_optimization_service.model.RoadNode;
import com.agrifleet.route_optimization_service.model.Vehicle;
import com.agrifleet.route_optimization_service.model.WeatherCondition;
import com.agrifleet.route_optimization_service.repository.RoadEdgeRepository;
import com.agrifleet.route_optimization_service.repository.RoadNodeRepository;
import com.agrifleet.route_optimization_service.repository.VehicleRepository;
import com.agrifleet.route_optimization_service.repository.WeatherConditionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the sample rural road network (matching the API documentation) on first boot:
 * depot, junctions, farms, a paved corridor, a muddy shortcut, a weight-limited bridge,
 * one harvester, and a demo weather zone.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoadNodeRepository nodeRepository;
    private final RoadEdgeRepository edgeRepository;
    private final VehicleRepository vehicleRepository;
    private final WeatherConditionRepository weatherRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (nodeRepository.count() > 0) {
            log.info("Road network already seeded ({} nodes) - skipping.", nodeRepository.count());
            return;
        }

        RoadNode depot = saveNode("Depot - Kandy", 7.2906, 80.6337, RoadNode.NodeType.DEPOT);
        RoadNode junctionA = saveNode("Junction A", 7.3200, 80.7000, RoadNode.NodeType.JUNCTION);
        RoadNode junctionB = saveNode("Junction B", 7.2500, 80.6100, RoadNode.NodeType.JUNCTION);
        RoadNode farmGampola = saveNode("Farm - Gampola", 7.1643, 80.5696, RoadNode.NodeType.FARM, "F-1001");
        RoadNode farmNawalapitiya = saveNode("Farm - Nawalapitiya", 7.0552, 80.5344, RoadNode.NodeType.FARM, "F-1002");

        // Paved highway corridor
        saveEdge(depot, junctionA, 7.5, RoadEdge.SurfaceType.PAVED, 60, null);
        saveEdge(junctionA, junctionB, 5.0, RoadEdge.SurfaceType.PAVED, 60, null);
        saveEdge(junctionB, farmNawalapitiya, 12.0, RoadEdge.SurfaceType.PAVED, 55, null);

        // Gravel road + muddy shortcut (both weather-sensitive)
        saveEdge(junctionB, farmGampola, 8.2, RoadEdge.SurfaceType.GRAVEL, 30, null);
        saveEdge(junctionA, farmGampola, 4.1, RoadEdge.SurfaceType.DIRT_TRACK, 15, null);

        // Weight-limited bridge between the two farms
        saveEdge(farmGampola, farmNawalapitiya, 3.0, RoadEdge.SurfaceType.BRIDGE, 25, 8.5);

        vehicleRepository.save(Vehicle.builder()
                .name("Harvester-07")
                .vehicleType(Vehicle.VehicleType.COMBINE_HARVESTER)
                .maxWeightTonnes(9.2)
                .currentNode(depot)
                .currentLatitude(depot.getLatitude())
                .currentLongitude(depot.getLongitude())
                .speedFactor(1.0)
                .isAvailable(true)
                .build());

        // Demo weather: heavy rain at Junction B makes the dirt-track shortcut expensive
        weatherRepository.save(WeatherCondition.builder()
                .node(junctionB)
                .condition(WeatherCondition.Condition.HEAVY_RAIN)
                .rainIntensity(0.8)
                .build());

        log.info("Seeded AgriFleet Task 1 sample network: {} nodes, {} edges, 1 vehicle, 1 weather zone.",
                nodeRepository.count(), edgeRepository.count());
    }

    private RoadNode saveNode(String name, double lat, double lng, RoadNode.NodeType type) {
        return saveNode(name, lat, lng, type, null);
    }

    private RoadNode saveNode(String name, double lat, double lng, RoadNode.NodeType type, String farmId) {
        return nodeRepository.save(RoadNode.builder()
                .name(name)
                .latitude(lat)
                .longitude(lng)
                .nodeType(type)
                .farmId(farmId)
                .isActive(true)
                .build());
    }

    private void saveEdge(RoadNode source, RoadNode target, double km,
                          RoadEdge.SurfaceType surface, double speed, Double weightLimit) {
        edgeRepository.save(RoadEdge.builder()
                .sourceNode(source)
                .targetNode(target)
                .distanceKm(km)
                .surfaceType(surface)
                .speedLimitKmh(speed)
                .weightLimitTonnes(weightLimit)
                .isBidirectional(true)
                .isActive(true)
                .build());
    }
}
