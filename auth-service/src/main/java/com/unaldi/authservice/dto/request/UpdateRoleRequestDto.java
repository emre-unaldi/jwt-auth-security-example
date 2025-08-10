package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for updating an existing role
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing role")
public class UpdateRoleRequestDto {

    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Role name must contain only uppercase letters and underscores")
    @Schema(description = "Updated role name", example = "SUPERVISOR")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Schema(description = "Updated role description")
    private String description;

    @Schema(description = "Updated active status")
    private Boolean active;

    @Schema(description = "Updated set of privilege IDs")
    private Set<Long> privilegeIds;

}
