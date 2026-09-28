package com.fitlog.exception;

/**
 * Exception thrown when a domain business rule is violated.
 * Examples: negative calories, invalid workout duration, etc.
 * Typically maps to HTTP 400 BAD REQUEST.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
