package com.unaldi.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response DTO for privilege statistics
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Privilege statistics response")
public class PrivilegeStatisticsResponseDto {

    @Schema(description = "Total number of privileges", example = "50")
    private long totalPrivileges;

    @Schema(description = "Number of active privileges", example = "45")
    private long activePrivileges;

    @Schema(description = "Number of system privileges", example = "20")
    private long systemPrivileges;

    @Schema(description = "Number of custom privileges", example = "30")
    private long customPrivileges;

    @Schema(description = "Number of unassigned privileges", example = "5")
    private long unassignedPrivileges;

    @Schema(description = "Resources with privileges")
    private Set<String> resources;

    @Schema(description = "Available actions")
    private Set<String> actions;

    @Schema(description = "Timestamp of statistics generation")
    private LocalDateTime generatedAt;

}
