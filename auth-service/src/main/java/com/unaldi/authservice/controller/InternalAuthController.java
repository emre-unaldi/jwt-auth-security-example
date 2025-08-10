package com.unaldi.authservice.controller;

import com.unaldi.authservice.dto.request.ValidateUserRolesRequestDto;
import com.unaldi.authservice.dto.response.ApiResponseDto;
import com.unaldi.authservice.dto.response.RoleValidationResponseDto;
import com.unaldi.authservice.service.RoleService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal REST controller for service-to-service communication.
 * These endpoints are not exposed publicly and are used by other microservices.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 11.08.2025
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
@Hidden // Hide from Swagger documentation
public class InternalAuthController {

    private final RoleService roleService;

    /**
     * Validates if the provided role names exist and are active
     *
     * @param request Validation request containing role names
     * @return Validation response
     */
    @PostMapping("/roles/validate")
    public ResponseEntity<ApiResponseDto<RoleValidationResponseDto>> validateRoles(@Valid @RequestBody ValidateUserRolesRequestDto request) {
        log.info("Internal role validation request for user: {}", request.getUserId());

        Set<String> validRoles = roleService.validateRoleNames(request.getRoleNames());
        Set<String> invalidRoles = new HashSet<>(request.getRoleNames());
        invalidRoles.removeAll(validRoles);

        RoleValidationResponseDto response = RoleValidationResponseDto.builder()
                .allValid(invalidRoles.isEmpty())
                .validRoles(validRoles)
                .invalidRoles(invalidRoles)
                .message(invalidRoles.isEmpty() ? "All roles are valid" : "Some roles are invalid")
                .validatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponseDto.success(response, "Role validation completed"));
    }

    /**
     * Health check endpoint for internal service communication
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponseDto<String>> health() {
        return ResponseEntity.ok(
                ApiResponseDto.success("Auth service is healthy", "Service is running")
        );
    }
}