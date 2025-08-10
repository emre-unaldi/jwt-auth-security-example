package com.unaldi.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for logout operation
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Logout response")
public class LogoutResponseDto {

    @Schema(description = "Logout success status", example = "true")
    private boolean success;

    @Schema(description = "Logout message", example = "Successfully logged out")
    private String message;

    @Schema(description = "Number of tokens revoked", example = "1")
    private int tokensRevoked;

    @Schema(description = "Logout timestamp")
    private LocalDateTime loggedOutAt;

}
