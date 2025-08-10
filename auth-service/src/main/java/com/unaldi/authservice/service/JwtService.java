package com.unaldi.authservice.service;

import com.unaldi.authservice.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

/**
 * Service for JWT token operations including generation, validation, and parsing.
 * Handles both access tokens and refresh tokens with different security configurations.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Generates an access token for a user
     *
     * @param userId User ID
     * @param email User email
     * @param roles Set of role names
     * @param privileges Set of privilege codes
     * @return JWT access token
     */
    public String generateAccessToken(Long userId, String email, Set<String> roles, Set<String> privileges) {
        log.debug("Generating access token for user: {}", email);

        Map<String, Object> claims = Map.of(
                "userId", userId,
                "email", email,
                "roles", roles,
                "privileges", privileges,
                "tokenType", "ACCESS"
        );

        return createToken(claims, email, jwtProperties.getExpiration().getAccessToken(),
                jwtProperties.getSecret().getAccessToken());
    }

    /**
     * Generates a refresh token for a user
     *
     * @param userId User ID
     * @param email User email
     * @return JWT refresh token
     */
    public String generateRefreshToken(Long userId, String email) {
        log.debug("Generating refresh token for user: {}", email);

        Map<String, Object> claims = Map.of(
                "userId", userId,
                "email", email,
                "tokenType", "REFRESH"
        );

        return createToken(claims, email, jwtProperties.getExpiration().getRefreshToken(),
                jwtProperties.getSecret().getRefreshToken());
    }

    /**
     * Validates an access token
     *
     * @param token The token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateAccessToken(String token) {
        return validateToken(token, jwtProperties.getSecret().getAccessToken());
    }

    /**
     * Validates a refresh token
     *
     * @param token The token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateRefreshToken(String token) {
        return validateToken(token, jwtProperties.getSecret().getRefreshToken());
    }

    /**
     * Extracts user ID from access token
     *
     * @param token JWT token
     * @return User ID
     */
    public Long extractUserIdFromAccessToken(String token) {
        Claims claims = extractAllClaims(token, jwtProperties.getSecret().getAccessToken());
        return claims.get("userId", Long.class);
    }

    /**
     * Extracts user ID from refresh token
     *
     * @param token JWT token
     * @return User ID
     */
    public Long extractUserIdFromRefreshToken(String token) {
        Claims claims = extractAllClaims(token, jwtProperties.getSecret().getRefreshToken());
        return claims.get("userId", Long.class);
    }

    /**
     * Extracts email from access token
     *
     * @param token JWT token
     * @return User email
     */
    public String extractEmailFromAccessToken(String token) {
        return extractClaim(token, Claims::getSubject, jwtProperties.getSecret().getAccessToken());
    }

    /**
     * Extracts email from refresh token
     *
     * @param token JWT token
     * @return User email
     */
    public String extractEmailFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject, jwtProperties.getSecret().getRefreshToken());
    }

    /**
     * Extracts roles from access token
     *
     * @param token JWT token
     * @return Set of role names
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractRolesFromAccessToken(String token) {
        Claims claims = extractAllClaims(token, jwtProperties.getSecret().getAccessToken());
        return new HashSet<>(((List<String>) claims.get("roles", java.util.List.class)));
    }

    /**
     * Extracts privileges from access token
     *
     * @param token JWT token
     * @return Set of privilege codes
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractPrivilegesFromAccessToken(String token) {
        Claims claims = extractAllClaims(token, jwtProperties.getSecret().getAccessToken());
        return new HashSet<>(((List<String>) claims.get("privileges", List.class)));
    }

    /**
     * Extracts expiration date from access token
     *
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpirationFromAccessToken(String token) {
        return extractClaim(token, Claims::getExpiration, jwtProperties.getSecret().getAccessToken());
    }

    /**
     * Extracts expiration date from refresh token
     *
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpirationFromRefreshToken(String token) {
        return extractClaim(token, Claims::getExpiration, jwtProperties.getSecret().getRefreshToken());
    }

    /**
     * Checks if access token is expired
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isAccessTokenExpired(String token) {
        return extractExpirationFromAccessToken(token).before(new Date());
    }

    /**
     * Checks if refresh token is expired
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isRefreshTokenExpired(String token) {
        return extractExpirationFromRefreshToken(token).before(new Date());
    }

    /**
     * Creates a JWT token with given claims
     *
     * @param claims Token claims
     * @param subject Token subject (email)
     * @param expiration Expiration time in milliseconds
     * @param secret Secret key
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration, String secret) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(secret))
                .compact();
    }

    /**
     * Validates a token with given secret
     *
     * @param token JWT token
     * @param secret Secret key
     * @return true if valid, false otherwise
     */
    private boolean validateToken(String token, String secret) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey(secret))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts a specific claim from token
     *
     * @param token JWT token
     * @param claimsResolver Function to extract claim
     * @param secret Secret key
     * @param <T> Type of claim
     * @return Claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secret) {
        Claims claims = extractAllClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from token
     *
     * @param token JWT token
     * @param secret Secret key
     * @return Claims object
     */
    private Claims extractAllClaims(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Gets signing key from secret string
     *
     * @param secret Secret string
     * @return SecretKey for signing
     */
    private SecretKey getSigningKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gets access token expiration time in seconds
     *
     * @return Expiration time in seconds
     */
    public Long getAccessTokenExpirationInSeconds() {
        return jwtProperties.getExpiration().getAccessToken() / 1000;
    }

    /**
     * Gets refresh token expiration time in seconds
     *
     * @return Expiration time in seconds
     */
    public Long getRefreshTokenExpirationInSeconds() {
        return jwtProperties.getExpiration().getRefreshToken() / 1000;
    }

}
