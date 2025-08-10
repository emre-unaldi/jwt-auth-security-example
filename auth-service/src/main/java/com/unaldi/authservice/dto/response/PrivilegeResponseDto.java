package com.unaldi.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Privilege information
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
@Schema(description = "Privilege information response")
public class PrivilegeResponseDto {

    @Schema(description = "Privilege ID", example = "1")
    private Long id;

    @Schema(description = "Privilege code", example = "USER_CREATE")
    private String code;

    @Schema(description = "Privilege name", example = "Create User")
    private String name;

    @Schema(description = "Privilege description", example = "Allows creating new user accounts")
    private String description;

    @Schema(description = "Resource this privilege applies to", example = "USER")
    private String resource;

    @Schema(description = "Action this privilege allows", example = "CREATE")
    private String action;

    @Schema(description = "Whether the privilege is active", example = "true")
    private boolean active;

    @Schema(description = "Whether this is a system privilege", example = "false")
    private boolean system;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdDate;

    @Schema(description = "Created by user email")
    private String createdBy;

    @Schema(description = "Last modification timestamp")
    private LocalDateTime lastModifiedDate;

    @Schema(description = "Last modified by user email")
    private String lastModifiedBy;

}
