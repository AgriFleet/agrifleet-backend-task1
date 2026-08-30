package com.agrifleet.route_optimization_service.service;

import com.agrifleet.route_optimization_service.model.RoadEdge.SurfaceType;
import com.agrifleet.route_optimization_service.model.WeatherCondition;
import org.springframework.stereotype.Component;

/**
 * Task 1 edge-cost model (doc §7.1).
 *
 * <pre>
 * cost(e) = (distance / speed) x 60 x surfaceFactor(s) x weatherMultiplier(e)
 * </pre>
 *
 * Weather penalties apply only to <b>unpaved</b> surfaces when weather-aware routing
 * is enabled. Bridge edges additionally carry a weight tolerance checked by the
 * route builder (overloaded bridges are pruned).
 */
@Component
public class CostModel {

    /** Friction multiplier per road surface. */
    public double surfaceFactor(SurfaceType surfaceType) {
        return switch (surfaceType) {
            case PAVED, BRIDGE -> 1.00;
            case GRAVEL -> 1.15;
            case MUD -> 1.35;
            case DIRT_TRACK -> 1.50;
        };
    }

    /**
     * Weather resistance multiplier for unpaved edges.
     * CLEAR -> 1.0, LIGHT_RAIN -> 1 + 0.5*I, HEAVY_RAIN -> 1 + 1.0*I, FLOOD -> 1 + 1.8*I.
     *
     * @param condition   current weather at the traversed node
     * @param intensity   rain intensity in [0,1]
     */
    public double weatherMultiplier(WeatherCondition.Condition condition, double intensity) {
        return switch (condition == null ? WeatherCondition.Condition.CLEAR : condition) {
            case CLEAR -> 1.0;
            case LIGHT_RAIN -> 1 + 0.5 * intensity;
            case HEAVY_RAIN -> 1 + 1.0 * intensity;
            case FLOOD -> 1 + 1.8 * intensity;
        };
    }

    /** Base travel time in minutes for a road segment. */
    public double travelTimeMinutes(double distanceKm, double speedKmh) {
        return (distanceKm / speedKmh) * 60.0;
    }

    /** Whether a surface is unpaved (eligible for weather penalties). */
    public boolean isUnpaved(SurfaceType surfaceType) {
        return surfaceType == SurfaceType.GRAVEL
                || surfaceType == SurfaceType.MUD
                || surfaceType == SurfaceType.DIRT_TRACK;
    }
}
