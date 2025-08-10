package com.unaldi.authservice.exception;

/**
 * Exception thrown when a token is invalid
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}