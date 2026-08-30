package com.agrifleet.route_optimization_service.controller;

import com.agrifleet.route_optimization_service.dto.WeatherDto;
import com.agrifleet.route_optimization_service.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for weather-aware road resistance ({@code /api/v1/weather}).
 */
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @PutMapping("/{nodeId}")
    public WeatherDto set(@PathVariable Long nodeId, @RequestBody WeatherDto dto) {
        return weatherService.setWeather(nodeId, dto);
    }

    @GetMapping
    public List<WeatherDto> list() {
        return weatherService.listWeather();
    }

    @GetMapping("/{nodeId}")
    public WeatherDto get(@PathVariable Long nodeId) {
        return weatherService.getWeather(nodeId);
    }
}
