package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.entity.RoadEdge;
import com.agrifleet.route_optimization_service.entity.RoadNode;
import com.agrifleet.route_optimization_service.model.Graph;
import com.agrifleet.route_optimization_service.model.Node;
import com.agrifleet.route_optimization_service.repository.RoadEdgeRepository;
import com.agrifleet.route_optimization_service.repository.RoadNodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphBuilderService {

    private final RoadNodeRepository roadNodeRepository;
    private final RoadEdgeRepository roadEdgeRepository;

    public GraphBuilderService(RoadNodeRepository roadNodeRepository, RoadEdgeRepository roadEdgeRepository) {
        this.roadNodeRepository = roadNodeRepository;
        this.roadEdgeRepository = roadEdgeRepository;
    }

    public Graph buildGraph() {
        Graph graph = new Graph();

        // get nodes from db
        List<RoadNode> roadNodes = roadNodeRepository.findAll();
        for (RoadNode roadNode : roadNodes) {
            Node node = new Node(
                    roadNode.getNodeId().intValue(),
                    roadNode.getNodeName(),
                    roadNode.getLat(),
                    roadNode.getLng()
            );
            graph.addNode(node);
        }

        // get edges from db
        List<RoadEdge> roadEdges = roadEdgeRepository.findAll();
        for (RoadEdge roadEdge : roadEdges) {
            graph.addEdge(
                    roadEdge.getUNode().getNodeId().intValue(),
                    roadEdge.getVNode().getNodeId().intValue(),
                    roadEdge.getComputedWeight()
            );
        }

        return graph;
    }
}
