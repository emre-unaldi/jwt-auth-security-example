package com.unaldi.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Request DTO for token validation
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to validate a token")
public class TokenValidationRequestDto {

    @NotBlank(message = "Token is required")
    @Schema(description = "Token to validate")
    private String token;

    @Schema(description = "Token type (ACCESS or REFRESH)", example = "ACCESS")
    private String tokenType = "ACCESS";

}