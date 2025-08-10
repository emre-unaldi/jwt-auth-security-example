package com.unaldi.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for role assignment validation
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Role validation response")
public class RoleValidationResponseDto {

    @Schema(description = "Whether all roles are valid", example = "true")
    private boolean allValid;

    @Schema(description = "Valid role names")
    private Set<String> validRoles;

    @Schema(description = "Invalid role names")
    private Set<String> invalidRoles;

    @Schema(description = "Validation message")
    private String message;

    @Schema(description = "Validation timestamp")
    private LocalDateTime validatedAt;

}