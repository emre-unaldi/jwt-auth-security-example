package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for removing privileges from a role
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to remove privileges from a role")
public class RemovePrivilegesFromRoleRequestDto {

    @NotNull(message = "Role ID is required")
    @Schema(description = "Role ID")
    private Long roleId;

    @NotNull(message = "Privilege IDs are required")
    @Schema(description = "Set of privilege IDs to remove")
    private Set<Long> privilegeIds;

}