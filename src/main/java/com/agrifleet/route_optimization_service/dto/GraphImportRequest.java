package com.agrifleet.route_optimization_service.dto;

import java.util.List;

/**
 * Payload for bulk graph import ({@code POST /graph/import}).
 * Edges reference existing node ids ({@code sourceNodeId}/{@code targetNodeId}).
 */
public record GraphImportRequest(
        List<NodeDto> nodes,
        List<EdgeDto> edges
) {}
