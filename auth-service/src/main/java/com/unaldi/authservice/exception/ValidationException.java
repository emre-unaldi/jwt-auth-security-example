package com.unaldi.authservice.exception;

/**
 * Exception thrown when validation fails
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}