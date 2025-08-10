package com.unaldi.authservice.exception;

/**
 * Exception thrown when a token has expired
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}