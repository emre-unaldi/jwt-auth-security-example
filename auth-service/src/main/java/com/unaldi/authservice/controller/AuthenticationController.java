package com.unaldi.authservice.controller;

import com.unaldi.authservice.dto.request.*;
import com.unaldi.authservice.dto.response.*;
import com.unaldi.authservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Handles user authentication, token management, and logout.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user and returns JWT tokens
     *
     * @param request Login request with credentials
     * @param httpRequest HTTP servlet request for extracting client information
     * @return Authentication response with tokens
     */
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and returns access and refresh tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponseDto<AuthenticationResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest
    ) {
        log.info("Login attempt for user: {}", request.getEmail());

        // Extract client information
        request.setIpAddress(getClientIpAddress(httpRequest));
        request.setUserAgent(httpRequest.getHeader("User-Agent"));

        AuthenticationResponseDto response = authenticationService.authenticate(request);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Authentication successful"));
    }

    /**
     * Refreshes access token using refresh token
     *
     * @param request Refresh token request
     * @return Token refresh response with new access token
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully refreshed token",
                    content = @Content(schema = @Schema(implementation = TokenRefreshResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid refresh token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponseDto<TokenRefreshResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("Token refresh request received");

        TokenRefreshResponseDto response = authenticationService.refreshToken(request);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Token refreshed successfully"));
    }

    /**
     * Validates a token
     *
     * @param request Token validation request
     * @return Token validation response
     */
    @PostMapping("/validate")
    @Operation(
            summary = "Validate token",
            description = "Validates an access or refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token validation completed",
                    content = @Content(schema = @Schema(implementation = TokenValidationResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponseDto<TokenValidationResponseDto>> validateToken(@Valid @RequestBody TokenValidationRequestDto request) {
        log.debug("Token validation request for type: {}", request.getTokenType());

        TokenValidationResponseDto response = authenticationService.validateToken(request);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Token validation completed"));
    }

    /**
     * Logs out a user
     *
     * @param request Logout request
     * @return Logout response
     */
    @PostMapping("/logout")
    @Operation(
            summary = "User logout",
            description = "Revokes user tokens and logs out from one or all devices",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged out",
                    content = @Content(schema = @Schema(implementation = LogoutResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<LogoutResponseDto>> logout(@Valid @RequestBody LogoutRequestDto request) {
        log.info("Logout request received");

        LogoutResponseDto response = authenticationService.logout(request);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Logout successful"));
    }

    /**
     * Gets the client IP address from the request
     *
     * @param request HTTP servlet request
     * @return Client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}