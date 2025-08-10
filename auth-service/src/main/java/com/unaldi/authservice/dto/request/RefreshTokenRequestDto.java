package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Request DTO for token refresh
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to refresh access token using refresh token")
class RefreshTokenRequestDto {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Valid refresh token")
    private String refreshToken;

    @Schema(description = "Device fingerprint for validation", example = "unique-device-id")
    private String deviceFingerprint;

}