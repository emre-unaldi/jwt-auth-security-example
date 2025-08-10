package com.unaldi.authservice.config;

import com.unaldi.authservice.security.CustomUserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuration for JPA auditing.
 * Provides the current user's email for audit fields.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 11.08.2025
 */
@Configuration
public class AuditorAwareConfiguration {

    /**
     * Provides AuditorAware bean for JPA auditing
     *
     * @return AuditorAware implementation
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    /**
     * Implementation of AuditorAware that returns the current user's email
     */
    private static class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }

            if (authentication.getPrincipal() instanceof CustomUserPrincipal) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
                return Optional.of(principal.getEmail());
            }

            return Optional.of("system");
        }
    }
}