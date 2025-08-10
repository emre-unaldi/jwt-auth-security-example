package com.unaldi.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for Role information
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Role information response")
public class RoleResponseDto {

    @Schema(description = "Role ID", example = "1")
    private Long id;

    @Schema(description = "Role name", example = "ADMIN")
    private String name;

    @Schema(description = "Role description", example = "System administrator with full access")
    private String description;

    @Schema(description = "Whether the role is active", example = "true")
    private boolean active;

    @Schema(description = "Whether this is a system role", example = "false")
    private boolean system;

    @Schema(description = "Associated privileges")
    private Set<PrivilegeResponseDto> privileges;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdDate;

    @Schema(description = "Created by user email")
    private String createdBy;

    @Schema(description = "Last modification timestamp")
    private LocalDateTime lastModifiedDate;

    @Schema(description = "Last modified by user email")
    private String lastModifiedBy;

}
