package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for creating a new role
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new role")
public class CreateRoleRequestDto {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Role name must contain only uppercase letters and underscores")
    @Schema(description = "Unique role name", example = "MANAGER")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Schema(description = "Role description", example = "Manager with limited admin privileges")
    private String description;

    @Schema(description = "Whether this role is active", example = "true")
    private boolean active = true;

    @Schema(description = "Set of privilege IDs to assign to this role")
    private Set<Long> privilegeIds;

}
