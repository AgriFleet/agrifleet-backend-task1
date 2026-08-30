package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.EdgeDto;
import com.agrifleet.route_optimization_service.model.RoadEdge.SurfaceType;
import com.agrifleet.route_optimization_service.service.GraphService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for road segments ({@code /api/v1/edges}).
 */
@RestController
@RequestMapping("/api/v1/edges")
@RequiredArgsConstructor
public class EdgeController {

    private final GraphService graphService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EdgeDto create(@RequestBody EdgeDto dto) {
        return graphService.createEdge(dto);
    }

    @GetMapping
    public List<EdgeDto> list(@RequestParam(required = false) Long sourceNodeId,
                              @RequestParam(required = false) SurfaceType surfaceType) {
        return graphService.listEdges(sourceNodeId, surfaceType);
    }

    @GetMapping("/{id}")
    public EdgeDto get(@PathVariable Long id) {
        return graphService.getEdge(id);
    }

    @PutMapping("/{id}")
    public EdgeDto update(@PathVariable Long id, @RequestBody EdgeDto dto) {
        return graphService.updateEdge(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        graphService.deleteEdge(id);
    }
}
