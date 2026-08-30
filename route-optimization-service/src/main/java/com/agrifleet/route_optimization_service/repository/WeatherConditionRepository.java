package com.agrifleet.route_optimization_service.repository;

import com.agrifleet.route_optimization_service.model.WeatherCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherConditionRepository extends JpaRepository<WeatherCondition, Long> {

    Optional<WeatherCondition> findByNodeId(Long nodeId);

    List<WeatherCondition> findByCondition(WeatherCondition.Condition condition);

    void deleteByNodeId(Long nodeId);
}
