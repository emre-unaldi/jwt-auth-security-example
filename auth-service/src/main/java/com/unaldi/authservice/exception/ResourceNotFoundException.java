package com.unaldi.authservice.exception;

/**
 * Exception thrown when a requested resource is not found
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}