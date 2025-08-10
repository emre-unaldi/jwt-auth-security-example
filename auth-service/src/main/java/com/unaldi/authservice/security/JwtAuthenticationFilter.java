package com.unaldi.authservice.security;

import com.unaldi.authservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT authentication filter that validates JWT tokens on each request.
 * Extracts user information and authorities from valid tokens.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Filters incoming requests to validate JWT tokens
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token != null && jwtService.validateAccessToken(token)) {
            try {
                // Extract user information from token
                Long userId = jwtService.extractUserIdFromAccessToken(token);
                String email = jwtService.extractEmailFromAccessToken(token);
                Set<String> roles = jwtService.extractRolesFromAccessToken(token);
                Set<String> privileges = jwtService.extractPrivilegesFromAccessToken(token);

                // Create authorities from roles and privileges
                Set<SimpleGrantedAuthority> authorities = new HashSet<>();

                // Add roles with ROLE_ prefix
                authorities.addAll(roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet()));

                // Add privileges as authorities
                authorities.addAll(privileges.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()));

                // Create authentication token
                CustomUserPrincipal principal = new CustomUserPrincipal(userId, email, roles, privileges);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Successfully authenticated user: {} with authorities: {}", email, authorities);
            } catch (Exception e) {
                log.error("Failed to set user authentication: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from request header
     *
     * @param request HTTP request
     * @return JWT token or null if not present
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Determines if the filter should not be applied to certain paths
     *
     * @param request HTTP request
     * @return true if filter should be skipped
     * @throws ServletException if error occurs
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/api/v1/auth/login") ||
                path.equals("/api/v1/auth/refresh") ||
                path.equals("/api/v1/auth/validate");
    }
}