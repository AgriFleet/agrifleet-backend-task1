package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.GraphImportRequest;
import com.agrifleet.route_optimization_service.dto.GraphStatsDto;
import com.agrifleet.route_optimization_service.dto.ImportResultDto;
import com.agrifleet.route_optimization_service.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for graph-wide utilities ({@code /api/v1/graph}).
 */
@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphService graphService;

    @PostMapping("/import")
    public ImportResultDto importGraph(@RequestBody GraphImportRequest request) {
        return graphService.importGraph(request);
    }

    @GetMapping("/stats")
    public GraphStatsDto stats() {
        return graphService.getStats();
    }
}
