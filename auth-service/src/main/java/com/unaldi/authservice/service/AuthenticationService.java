package com.unaldi.authservice.service;

import com.unaldi.authservice.dto.request.LoginRequestDto;
import com.unaldi.authservice.dto.request.LogoutRequestDto;
import com.unaldi.authservice.dto.request.RefreshTokenRequestDto;
import com.unaldi.authservice.dto.request.TokenValidationRequestDto;
import com.unaldi.authservice.dto.response.AuthenticationResponseDto;
import com.unaldi.authservice.dto.response.LogoutResponseDto;
import com.unaldi.authservice.dto.response.TokenRefreshResponseDto;
import com.unaldi.authservice.dto.response.TokenValidationResponseDto;
import com.unaldi.authservice.model.RefreshToken;
import com.unaldi.authservice.exception.AuthenticationException;
import com.unaldi.authservice.exception.InvalidTokenException;
import com.unaldi.authservice.feign.UserServiceFeignClient;
import com.unaldi.authservice.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service implementation for authentication operations.
 * Handles login, logout, token generation, validation, and refresh.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceFeignClient userServiceFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    /**
     * Authenticates a user and generates tokens
     *
     * @param request Login request with credentials
     * @return Authentication response with tokens
     * @throws AuthenticationException if authentication fails
     */
    @Transactional
    public AuthenticationResponseDto authenticate(LoginRequestDto request) {
        log.info("Authenticating user: {}", request.getEmail());

        try {
            // Get user details from user-service
            var userDetails = userServiceFeignClient.getUserByEmail(request.getEmail());

            if (userDetails == null || !userDetails.isActive()) {
                throw new AuthenticationException("Invalid credentials or inactive account");
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                throw new AuthenticationException("Invalid credentials");
            }

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getRoles(),
                    userDetails.getPrivileges()
            );

            String refreshTokenString = generateAndSaveRefreshToken(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    request.getDeviceFingerprint(),
                    request.getIpAddress(),
                    request.getUserAgent()
            );

            // Store user tokens in Redis for tracking
            storeUserTokenInRedis(userDetails.getId(), refreshTokenString);

            return AuthenticationResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshTokenString)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                    .userId(userDetails.getId())
                    .email(userDetails.getEmail())
                    .roles(userDetails.getRoles())
                    .privileges(userDetails.getPrivileges())
                    .authenticatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getEmail(), e);
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Refreshes access token using refresh token
     *
     * @param request Refresh token request
     * @return Token refresh response with new access token
     * @throws InvalidTokenException if refresh token is invalid
     */
    @Transactional
    public TokenRefreshResponseDto refreshToken(RefreshTokenRequestDto request) {
        log.info("Refreshing token");

        String refreshTokenString = request.getRefreshToken();

        // Check if token is blacklisted
        if (isTokenBlacklisted(refreshTokenString)) {
            throw new InvalidTokenException("Token has been revoked");
        }

        // Validate refresh token
        if (!jwtService.validateRefreshToken(refreshTokenString)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        // Find refresh token in database
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenAndNotDeleted(refreshTokenString)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

        // Check if token is valid
        if (!refreshToken.isValid()) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        // Verify device fingerprint if provided
        if (request.getDeviceFingerprint() != null &&
                !request.getDeviceFingerprint().equals(refreshToken.getDeviceFingerprint())) {
            log.warn("Device fingerprint mismatch for token: {}", refreshToken.getId());
        }

        // Get user details from user-service
        var userDetails = userServiceFeignClient.getUserById(refreshToken.getUserId());

        if (userDetails == null || !userDetails.isActive()) {
            throw new InvalidTokenException("User not found or inactive");
        }

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getRoles(),
                userDetails.getPrivileges()
        );

        // Update refresh token usage
        refreshToken.incrementUsage();
        refreshTokenRepository.save(refreshToken);

        return TokenRefreshResponseDto.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirationInSeconds())
                .refreshedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Validates a token
     *
     * @param request Token validation request
     * @return Token validation response
     */
    public TokenValidationResponseDto validateToken(TokenValidationRequestDto request) {
        log.debug("Validating token of type: {}", request.getTokenType());

        boolean isValid = false;
        String message = "Invalid token";
        Long userId = null;
        String email = null;
        Set<String> roles = null;
        Set<String> privileges = null;
        LocalDateTime expiresAt = null;

        try {
            if ("ACCESS".equalsIgnoreCase(request.getTokenType())) {
                isValid = jwtService.validateAccessToken(request.getToken());
                if (isValid) {
                    userId = jwtService.extractUserIdFromAccessToken(request.getToken());
                    email = jwtService.extractEmailFromAccessToken(request.getToken());
                    roles = jwtService.extractRolesFromAccessToken(request.getToken());
                    privileges = jwtService.extractPrivilegesFromAccessToken(request.getToken());
                    var expDate = jwtService.extractExpirationFromAccessToken(request.getToken());
                    expiresAt = LocalDateTime.ofInstant(expDate.toInstant(), java.time.ZoneId.systemDefault());
                    message = "Valid access token";
                }
            } else if ("REFRESH".equalsIgnoreCase(request.getTokenType())) {
                isValid = jwtService.validateRefreshToken(request.getToken()) &&
                        !isTokenBlacklisted(request.getToken());
                if (isValid) {
                    userId = jwtService.extractUserIdFromRefreshToken(request.getToken());
                    email = jwtService.extractEmailFromRefreshToken(request.getToken());
                    var expDate = jwtService.extractExpirationFromRefreshToken(request.getToken());
                    expiresAt = LocalDateTime.ofInstant(expDate.toInstant(), java.time.ZoneId.systemDefault());
                    message = "Valid refresh token";
                }
            }
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            message = "Token validation failed: " + e.getMessage();
        }

        return TokenValidationResponseDto.builder()
                .valid(isValid)
                .tokenType(request.getTokenType())
                .userId(userId)
                .email(email)
                .roles(roles)
                .privileges(privileges)
                .expiresAt(expiresAt)
                .message(message)
                .build();
    }

    /**
     * Logs out a user by revoking tokens
     *
     * @param request Logout request
     * @return Logout response
     */
    @Transactional
    public LogoutResponseDto logout(LogoutRequestDto request) {
        log.info("Processing logout request");

        int tokensRevoked = 0;

        if (request.isLogoutFromAllDevices() && request.getUserId() != null) {
            // Revoke all tokens for the user
            refreshTokenRepository.revokeAllTokensByUserId(
                    request.getUserId(),
                    "User logged out from all devices"
            );

            // Clear user tokens from Redis
            clearUserTokensFromRedis(request.getUserId());

            // Count revoked tokens
            tokensRevoked = (int) refreshTokenRepository
                    .countActiveTokensByUserId(request.getUserId(), LocalDateTime.now());

        } else if (request.getRefreshToken() != null) {
            // Revoke specific refresh token
            refreshTokenRepository.revokeToken(
                    request.getRefreshToken(),
                    "User logged out"
            );

            // Add to blacklist
            blacklistToken(request.getRefreshToken());
            tokensRevoked = 1;
        }

        return LogoutResponseDto.builder()
                .success(true)
                .message("Successfully logged out")
                .tokensRevoked(tokensRevoked)
                .loggedOutAt(LocalDateTime.now())
                .build();
    }

    /**
     * Generates and saves a new refresh token
     */
    private String generateAndSaveRefreshToken(Long userId, String email, String deviceFingerprint,
                                               String ipAddress, String userAgent) {
        // Generate unique token
        String tokenString = UUID.randomUUID().toString();

        // Calculate expiry
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtService.getRefreshTokenExpirationInSeconds());

        // Create refresh token entity
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenString)
                .userId(userId)
                .userEmail(email)
                .expiryDate(expiryDate)
                .deviceFingerprint(deviceFingerprint)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        // Save to database
        refreshTokenRepository.save(refreshToken);

        return tokenString;
    }

    /**
     * Checks if a token is blacklisted in Redis
     */
    private boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }

    /**
     * Adds a token to the blacklist in Redis
     */
    private void blacklistToken(String token) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "revoked",
                jwtService.getRefreshTokenExpirationInSeconds(),
                TimeUnit.SECONDS
        );
    }

    /**
     * Stores user token in Redis for tracking
     */
    private void storeUserTokenInRedis(Long userId, String token) {
        String key = USER_TOKENS_PREFIX + userId;
        redisTemplate.opsForSet().add(key, token);
        redisTemplate.expire(key, jwtService.getRefreshTokenExpirationInSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Clears all user tokens from Redis
     */
    private void clearUserTokensFromRedis(Long userId) {
        redisTemplate.delete(USER_TOKENS_PREFIX + userId);
    }
}