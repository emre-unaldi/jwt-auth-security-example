package com.unaldi.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for JWT tokens.
 * Maps JWT-related properties from application.yml.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret keys for token signing
     */
    private Secret secret = new Secret();

    /**
     * Token expiration times in milliseconds
     */
    private Expiration expiration = new Expiration();

    /**
     * Inner class for secret keys
     */
    @Data
    public static class Secret {
        /**
         * Secret key for access token signing
         */
        private String accessToken;

        /**
         * Secret key for refresh token signing
         */
        private String refreshToken;
    }

    /**
     * Inner class for expiration times
     */
    @Data
    public static class Expiration {
        /**
         * Access token expiration time in milliseconds
         */
        private Long accessToken;

        /**
         * Refresh token expiration time in milliseconds
         */
        private Long refreshToken;
    }

}