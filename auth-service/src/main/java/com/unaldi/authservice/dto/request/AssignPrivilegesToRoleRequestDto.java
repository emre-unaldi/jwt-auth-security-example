package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for assigning privileges to a role
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to assign privileges to a role")
public class AssignPrivilegesToRoleRequestDto {

    @NotNull(message = "Role ID is required")
    @Schema(description = "Role ID")
    private Long roleId;

    @NotNull(message = "Privilege IDs are required")
    @Schema(description = "Set of privilege IDs to assign")
    private Set<Long> privilegeIds;

    @Schema(description = "Whether to replace existing privileges or add to them", example = "false")
    private boolean replaceExisting = false;

}