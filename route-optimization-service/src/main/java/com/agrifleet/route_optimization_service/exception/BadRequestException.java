package com.agrifleet.route_optimization_service.exception;

/**
 * Thrown when a request payload is invalid. Maps to HTTP 400.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
