package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for logout
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Logout request")
class LogoutRequestDto {

    @Schema(description = "Refresh token to revoke")
    private String refreshToken;

    @Schema(description = "Flag to logout from all devices", example = "false")
    private boolean logoutFromAllDevices = false;

    @Schema(description = "User ID for logout from all devices")
    private Long userId;

}