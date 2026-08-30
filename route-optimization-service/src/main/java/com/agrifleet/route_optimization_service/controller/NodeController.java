package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.NodeDto;
import com.agrifleet.route_optimization_service.model.RoadNode.NodeType;
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
 * REST API for graph vertices ({@code /api/v1/nodes}).
 */
@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {

    private final GraphService graphService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NodeDto create(@RequestBody NodeDto dto) {
        return graphService.createNode(dto);
    }

    @GetMapping
    public List<NodeDto> list(@RequestParam(required = false) NodeType nodeType,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) Boolean active) {
        return graphService.listNodes(nodeType, search, active);
    }

    @GetMapping("/{id}")
    public NodeDto get(@PathVariable Long id) {
        return graphService.getNode(id);
    }

    @PutMapping("/{id}")
    public NodeDto update(@PathVariable Long id, @RequestBody NodeDto dto) {
        return graphService.updateNode(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        graphService.deleteNode(id);
    }
}
