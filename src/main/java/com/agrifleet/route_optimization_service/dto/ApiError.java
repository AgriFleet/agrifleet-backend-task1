package com.agrifleet.route_optimization_service.dto;

/**
 * Uniform error payload returned by the global exception handler.
 */
public record ApiError(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {}
