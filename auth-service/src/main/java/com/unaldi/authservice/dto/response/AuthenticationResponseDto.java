package com.unaldi.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for successful authentication
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Authentication response containing tokens and user information")
public class AuthenticationResponseDto {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Refresh token for obtaining new access tokens", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiration time in seconds", example = "900")
    private Long expiresIn;

    @Schema(description = "User ID", example = "123")
    private Long userId;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "User roles", example = "[\"USER\", \"ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "User privileges", example = "[\"USER_READ\", \"USER_WRITE\"]")
    private Set<String> privileges;

    @Schema(description = "Authentication timestamp")
    private LocalDateTime authenticatedAt;

}
