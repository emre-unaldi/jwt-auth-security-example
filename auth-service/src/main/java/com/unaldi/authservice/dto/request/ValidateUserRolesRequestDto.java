package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for assigning a role to a user (used by user-service via Feign)
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to validate user roles")
public class ValidateUserRolesRequestDto {

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID")
    private Long userId;

    @NotNull(message = "Role names are required")
    @Schema(description = "Set of role names to validate")
    private Set<String> roleNames;

}
