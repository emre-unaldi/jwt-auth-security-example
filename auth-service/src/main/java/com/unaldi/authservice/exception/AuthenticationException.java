package com.unaldi.authservice.exception;

/**
 * Exception thrown when authentication fails
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}