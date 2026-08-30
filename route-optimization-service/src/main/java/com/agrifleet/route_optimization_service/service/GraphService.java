package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.dto.EdgeDto;
import com.agrifleet.route_optimization_service.dto.GraphImportRequest;
import com.agrifleet.route_optimization_service.dto.GraphStatsDto;
import com.agrifleet.route_optimization_service.dto.ImportResultDto;
import com.agrifleet.route_optimization_service.dto.NodeDto;
import com.agrifleet.route_optimization_service.exception.BadRequestException;
import com.agrifleet.route_optimization_service.exception.ResourceNotFoundException;
import com.agrifleet.route_optimization_service.model.RoadEdge;
import com.agrifleet.route_optimization_service.model.RoadEdge.SurfaceType;
import com.agrifleet.route_optimization_service.model.RoadNode;
import com.agrifleet.route_optimization_service.model.RoadNode.NodeType;
import com.agrifleet.route_optimization_service.repository.RoadEdgeRepository;
import com.agrifleet.route_optimization_service.repository.RoadNodeRepository;
import com.agrifleet.route_optimization_service.repository.VehicleRepository;
import com.agrifleet.route_optimization_service.repository.WeatherConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD + utilities for the road-network graph (nodes &amp; edges).
 * This is the adjacency-list data owner used by the routing engine (Commit 3).
 */
@Service
@RequiredArgsConstructor
public class GraphService {

    private final RoadNodeRepository nodeRepository;
    private final RoadEdgeRepository edgeRepository;
    private final WeatherConditionRepository weatherRepository;
    private final VehicleRepository vehicleRepository;

    // ------------------------------------------------------------------
    // Nodes
    // ------------------------------------------------------------------

    public NodeDto createNode(NodeDto dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BadRequestException("name is required");
        }
        if (dto.latitude() == null || dto.longitude() == null) {
            throw new BadRequestException("latitude and longitude are required");
        }
        if (dto.latitude() < -90 || dto.latitude() > 90
                || dto.longitude() < -180 || dto.longitude() > 180) {
            throw new BadRequestException("latitude/longitude out of range");
        }
        if (dto.nodeType() == null) {
            throw new BadRequestException("nodeType is required (JUNCTION, DEPOT or FARM)");
        }

        RoadNode node = RoadNode.builder()
                .name(dto.name().trim())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .nodeType(dto.nodeType())
                .farmId(dto.farmId())
                .isActive(dto.isActive() != null ? dto.isActive() : true)
                .build();
        return NodeDto.from(nodeRepository.save(node));
    }

    public NodeDto getNode(Long id) {
        return NodeDto.from(findNode(id));
    }

    public List<NodeDto> listNodes(NodeType type, String search, Boolean active) {
        List<RoadNode> nodes;
        if (type != null) {
            nodes = nodeRepository.findByNodeType(type);
        } else if (search != null && !search.isBlank()) {
            nodes = nodeRepository.findByNameContainingIgnoreCase(search.trim());
        } else if (active != null) {
            nodes = nodeRepository.findByIsActive(active);
        } else {
            nodes = nodeRepository.findByIsActiveTrue();
        }
        return nodes.stream().map(NodeDto::from).toList();
    }

    public NodeDto updateNode(Long id, NodeDto dto) {
        RoadNode node = findNode(id);
        if (dto.name() != null && !dto.name().isBlank()) node.setName(dto.name().trim());
        if (dto.latitude() != null) node.setLatitude(dto.latitude());
        if (dto.longitude() != null) node.setLongitude(dto.longitude());
        if (dto.nodeType() != null) node.setNodeType(dto.nodeType());
        if (dto.farmId() != null) node.setFarmId(dto.farmId());
        if (dto.isActive() != null) node.setIsActive(dto.isActive());
        return NodeDto.from(nodeRepository.save(node));
    }

    @Transactional
    public void deleteNode(Long id) {
        RoadNode node = findNode(id);
        // Remove dependent rows first to satisfy SQLite foreign-key constraints.
        edgeRepository.deleteAll(edgeRepository.findBySourceNodeIdOrTargetNodeId(id, id));
        weatherRepository.deleteByNodeId(id);
        vehicleRepository.findByCurrentNodeId(id).forEach(v -> {
            v.setCurrentNode(null);
            vehicleRepository.save(v);
        });
        nodeRepository.delete(node);
    }

    // ------------------------------------------------------------------
    // Edges
    // ------------------------------------------------------------------

    public EdgeDto createEdge(EdgeDto dto) {
        if (dto.sourceNodeId() == null || dto.targetNodeId() == null) {
            throw new BadRequestException("sourceNodeId and targetNodeId are required");
        }
        if (dto.sourceNodeId().equals(dto.targetNodeId())) {
            throw new BadRequestException("source and target node must be different");
        }
        if (dto.distanceKm() == null || dto.distanceKm() <= 0) {
            throw new BadRequestException("distanceKm must be greater than 0");
        }
        if (dto.speedLimitKmh() == null || dto.speedLimitKmh() <= 0) {
            throw new BadRequestException("speedLimitKmh must be greater than 0");
        }
        if (dto.surfaceType() == null) {
            throw new BadRequestException("surfaceType is required");
        }
        if (edgeRepository.existsBySourceNodeIdAndTargetNodeId(dto.sourceNodeId(), dto.targetNodeId())) {
            throw new BadRequestException("An edge already exists between these nodes");
        }

        RoadNode source = findNode(dto.sourceNodeId());
        RoadNode target = findNode(dto.targetNodeId());
        RoadEdge edge = RoadEdge.builder()
                .sourceNode(source)
                .targetNode(target)
                .distanceKm(dto.distanceKm())
                .surfaceType(dto.surfaceType())
                .speedLimitKmh(dto.speedLimitKmh())
                .weightLimitTonnes(dto.weightLimitTonnes())
                .isBidirectional(dto.isBidirectional() != null ? dto.isBidirectional() : true)
                .isActive(dto.isActive() != null ? dto.isActive() : true)
                .build();
        return EdgeDto.from(edgeRepository.save(edge));
    }

    public EdgeDto getEdge(Long id) {
        return EdgeDto.from(findEdge(id));
    }

    public List<EdgeDto> listEdges(Long sourceNodeId, SurfaceType surfaceType) {
        List<RoadEdge> edges;
        if (sourceNodeId != null) {
            edges = edgeRepository.findBySourceNodeId(sourceNodeId);
        } else if (surfaceType != null) {
            edges = edgeRepository.findBySurfaceType(surfaceType);
        } else {
            edges = edgeRepository.findByIsActiveTrue();
        }
        return edges.stream().map(EdgeDto::from).toList();
    }

    public EdgeDto updateEdge(Long id, EdgeDto dto) {
        RoadEdge edge = findEdge(id);
        if (dto.distanceKm() != null && dto.distanceKm() <= 0) {
            throw new BadRequestException("distanceKm must be greater than 0");
        }
        if (dto.speedLimitKmh() != null && dto.speedLimitKmh() <= 0) {
            throw new BadRequestException("speedLimitKmh must be greater than 0");
        }
        if (dto.sourceNodeId() != null) edge.setSourceNode(findNode(dto.sourceNodeId()));
        if (dto.targetNodeId() != null) edge.setTargetNode(findNode(dto.targetNodeId()));
        if (dto.distanceKm() != null) edge.setDistanceKm(dto.distanceKm());
        if (dto.surfaceType() != null) edge.setSurfaceType(dto.surfaceType());
        if (dto.speedLimitKmh() != null) edge.setSpeedLimitKmh(dto.speedLimitKmh());
        if (dto.weightLimitTonnes() != null) edge.setWeightLimitTonnes(dto.weightLimitTonnes());
        if (dto.isBidirectional() != null) edge.setIsBidirectional(dto.isBidirectional());
        if (dto.isActive() != null) edge.setIsActive(dto.isActive());
        return EdgeDto.from(edgeRepository.save(edge));
    }

    public void deleteEdge(Long id) {
        edgeRepository.delete(findEdge(id));
    }

    // ------------------------------------------------------------------
    // Graph utilities
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public GraphStatsDto getStats() {
        long vertices = nodeRepository.count();
        long edges = edgeRepository.count();
        long depots = nodeRepository.countByNodeType(NodeType.DEPOT);
        long farms = nodeRepository.countByNodeType(NodeType.FARM);
        long bridges = edgeRepository.countBySurfaceType(SurfaceType.BRIDGE);

        double pavedKm = edgeRepository.findAll().stream()
                .filter(e -> e.getSurfaceType() == SurfaceType.PAVED
                        || e.getSurfaceType() == SurfaceType.BRIDGE)
                .mapToDouble(RoadEdge::getDistanceKm)
                .sum();
        double unpavedKm = edgeRepository.findAll().stream()
                .filter(e -> e.getSurfaceType() == SurfaceType.GRAVEL
                        || e.getSurfaceType() == SurfaceType.MUD
                        || e.getSurfaceType() == SurfaceType.DIRT_TRACK)
                .mapToDouble(RoadEdge::getDistanceKm)
                .sum();

        return new GraphStatsDto(vertices, edges, depots, farms, bridges,
                round2(pavedKm), round2(unpavedKm));
    }

    @Transactional
    public ImportResultDto importGraph(GraphImportRequest request) {
        int nodesImported = 0;
        int edgesImported = 0;

        if (request.nodes() != null) {
            for (NodeDto node : request.nodes()) {
                createNode(node);
                nodesImported++;
            }
        }
        if (request.edges() != null) {
            for (EdgeDto edge : request.edges()) {
                createEdge(edge);
                edgesImported++;
            }
        }
        return new ImportResultDto(nodesImported, edgesImported,
                nodeRepository.count(), edgeRepository.count());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private RoadNode findNode(Long id) {
        return nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Road node with id " + id + " does not exist"));
    }

    private RoadEdge findEdge(Long id) {
        return edgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Road edge with id " + id + " does not exist"));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
