package com.unaldi.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Response success status", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;

    @Schema(description = "Request path")
    private String path;

    /**
     * Creates a successful response
     *
     * @param data The response data
     * @param message The success message
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful response with default message
     *
     * @param data The response data
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }

    /**
     * Creates an error response
     *
     * @param message The error message
     * @param <T> The type of data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
