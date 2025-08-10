package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for changing password with token
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to change password using current password")
class ChangePasswordRequestDto {

    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    @Schema(description = "New password")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Confirm new password")
    private String confirmPassword;

    @Schema(description = "Flag to logout from all devices after password change", example = "true")
    private boolean logoutAllDevices = true;

}