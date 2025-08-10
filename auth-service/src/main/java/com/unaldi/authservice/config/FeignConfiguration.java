package com.unaldi.authservice.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Configuration for Feign clients.
 * Configures interceptors and error handling for service-to-service communication.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 11.08.2025
 */
@Slf4j
@Configuration
public class FeignConfiguration {

    /**
     * Provides request interceptor to add authentication headers
     *
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Add internal service header
            requestTemplate.header("X-Internal-Service", "auth-service");

            // Forward authentication token if available
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() != null) {
                requestTemplate.header("Authorization", "Bearer " + authentication.getCredentials());
            }
        };
    }

    /**
     * Provides custom error decoder for Feign clients
     *
     * @return ErrorDecoder
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    /**
     * Custom error decoder implementation
     */
    @Slf4j
    private static class CustomFeignErrorDecoder implements ErrorDecoder {

        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, feign.Response response) {
            log.error("Feign error - Method: {}, Status: {}, Reason: {}", methodKey, response.status(), response.reason());

            return switch (response.status()) {
                case 400 -> new feign.FeignException.BadRequest(response.reason(), response.request(), null, response.headers());
                case 401 -> new feign.FeignException.Unauthorized(response.reason(), response.request(), null, response.headers());
                case 403 -> new feign.FeignException.Forbidden(response.reason(), response.request(), null, response.headers());
                case 404 -> new feign.FeignException.NotFound(response.reason(), response.request(), null, response.headers());
                case 500 -> new feign.FeignException.InternalServerError(response.reason(), response.request(), null, response.headers());
                default -> defaultErrorDecoder.decode(methodKey, response);
            };
        }
    }
}