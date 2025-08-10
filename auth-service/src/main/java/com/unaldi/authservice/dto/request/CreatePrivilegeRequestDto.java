package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new privilege
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new privilege")
public class CreatePrivilegeRequestDto {

    @NotBlank(message = "Privilege code is required")
    @Size(min = 3, max = 100, message = "Privilege code must be between 3 and 100 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Privilege code must contain only uppercase letters and underscores")
    @Schema(description = "Unique privilege code", example = "USER_CREATE")
    private String code;

    @NotBlank(message = "Privilege name is required")
    @Size(min = 3, max = 100, message = "Privilege name must be between 3 and 100 characters")
    @Schema(description = "Human-readable privilege name", example = "Create User")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Privilege description", example = "Allows creating new user accounts")
    private String description;

    @NotBlank(message = "Resource is required")
    @Size(max = 50, message = "Resource cannot exceed 50 characters")
    @Schema(description = "Resource this privilege applies to", example = "USER")
    private String resource;

    @NotBlank(message = "Action is required")
    @Size(max = 50, message = "Action cannot exceed 50 characters")
    @Schema(description = "Action this privilege allows", example = "CREATE")
    private String action;

    @Schema(description = "Whether this privilege is active", example = "true")
    private boolean active = true;
}
