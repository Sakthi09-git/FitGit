package com.fitlog.exception;

/**
 * Exception thrown when a requested resource (User, Workout, Meal, Goal) is not found.
 * Typically maps to HTTP 404 NOT FOUND.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
