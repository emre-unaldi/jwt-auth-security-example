package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing privilege
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing privilege")
public class UpdatePrivilegeRequestDto {

    @Size(min = 3, max = 100, message = "Privilege name must be between 3 and 100 characters")
    @Schema(description = "Updated privilege name", example = "Create User Account")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Updated privilege description")
    private String description;

    @Schema(description = "Updated active status")
    private Boolean active;

}
