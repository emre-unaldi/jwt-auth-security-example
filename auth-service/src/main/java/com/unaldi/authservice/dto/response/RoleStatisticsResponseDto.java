package com.unaldi.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for role statistics
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Role statistics response")
public class RoleStatisticsResponseDto {

    @Schema(description = "Total number of roles", example = "10")
    private long totalRoles;

    @Schema(description = "Number of active roles", example = "8")
    private long activeRoles;

    @Schema(description = "Number of system roles", example = "3")
    private long systemRoles;

    @Schema(description = "Number of custom roles", example = "7")
    private long customRoles;

    @Schema(description = "Average privileges per role", example = "5.2")
    private double averagePrivilegesPerRole;

    @Schema(description = "Timestamp of statistics generation")
    private LocalDateTime generatedAt;

}
