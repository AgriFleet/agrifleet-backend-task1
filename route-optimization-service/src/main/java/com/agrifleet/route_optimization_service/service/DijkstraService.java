package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.algorithm.DijkstraAlgorithm;
import com.agrifleet.route_optimization_service.model.Graph;
import com.agrifleet.route_optimization_service.model.RouteResult;
import org.springframework.stereotype.Service;

@Service
public class DijkstraService {

    private final GraphBuilderService graphBuilderService;

    public DijkstraService(GraphBuilderService graphBuilderService) {
        this.graphBuilderService = graphBuilderService;
    }

    public RouteResult executeOptimization(int startNodeId, int endNodeId) {
        // build graph from db
        Graph graph = graphBuilderService.buildGraph();

        // run algorithm
        DijkstraAlgorithm algorithm = new DijkstraAlgorithm();
        RouteResult result = algorithm.findShortestPath(graph, startNodeId, endNodeId);

        // save to db

        return result;
    }
}
