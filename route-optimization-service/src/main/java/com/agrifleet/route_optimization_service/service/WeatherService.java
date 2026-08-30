package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.dto.WeatherDto;
import com.agrifleet.route_optimization_service.exception.BadRequestException;
import com.agrifleet.route_optimization_service.exception.ResourceNotFoundException;
import com.agrifleet.route_optimization_service.model.RoadNode;
import com.agrifleet.route_optimization_service.model.WeatherCondition;
import com.agrifleet.route_optimization_service.repository.RoadNodeRepository;
import com.agrifleet.route_optimization_service.repository.WeatherConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manages weather states per road node - the input that drives the
 * weather-aware road-resistance penalty in the routing engine (Commit 3).
 */
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherConditionRepository weatherRepository;
    private final RoadNodeRepository nodeRepository;

    public WeatherDto setWeather(Long nodeId, WeatherDto dto) {
        RoadNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Road node with id " + nodeId + " does not exist"));

        double intensity = dto.rainIntensity() != null ? dto.rainIntensity() : 0.0;
        if (intensity < 0.0 || intensity > 1.0) {
            throw new BadRequestException("rainIntensity must be between 0.0 and 1.0");
        }

        WeatherCondition weather = weatherRepository.findByNodeId(nodeId)
                .orElseGet(() -> WeatherCondition.builder().node(node).build());
        weather.setNode(node);
        weather.setCondition(dto.condition() != null ? dto.condition() : WeatherCondition.Condition.CLEAR);
        weather.setRainIntensity(intensity);
        return WeatherDto.from(weatherRepository.save(weather));
    }

    public WeatherDto getWeather(Long nodeId) {
        return WeatherDto.from(weatherRepository.findByNodeId(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No weather recorded for node " + nodeId)));
    }

    public List<WeatherDto> listWeather() {
        return weatherRepository.findAll().stream()
                .map(WeatherDto::from)
                .toList();
    }
}
