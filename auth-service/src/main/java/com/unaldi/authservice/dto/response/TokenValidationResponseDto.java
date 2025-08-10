package com.unaldi.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for token validation
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation response")
public class TokenValidationResponseDto {

    @Schema(description = "Whether the token is valid", example = "true")
    private boolean valid;

    @Schema(description = "Token type", example = "ACCESS")
    private String tokenType;

    @Schema(description = "User ID from token", example = "123")
    private Long userId;

    @Schema(description = "User email from token", example = "user@example.com")
    private String email;

    @Schema(description = "Roles from token")
    private Set<String> roles;

    @Schema(description = "Privileges from token")
    private Set<String> privileges;

    @Schema(description = "Token expiration timestamp")
    private LocalDateTime expiresAt;

    @Schema(description = "Validation message")
    private String message;

}
