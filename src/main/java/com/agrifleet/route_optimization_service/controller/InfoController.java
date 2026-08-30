package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.InfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Service metadata / health endpoint ({@code GET /api/v1/info}).
 */
@RestController
@RequestMapping("/api/v1/info")
public class InfoController {

    @Value("${spring.application.name:route-optimization-service}")
    private String serviceName;

    @GetMapping
    public InfoDto info() {
        return new InfoDto(
                serviceName,
                "1.0.0",
                List.of("ASTAR", "DIJKSTRA"),
                "HAVERSINE",
                true,
                "UP");
    }
}
