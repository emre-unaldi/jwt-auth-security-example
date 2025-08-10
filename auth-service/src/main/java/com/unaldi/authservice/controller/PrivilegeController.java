package com.unaldi.authservice.controller;

import com.unaldi.authservice.dto.request.CreatePrivilegeRequestDto;
import com.unaldi.authservice.dto.request.UpdatePrivilegeRequestDto;
import com.unaldi.authservice.dto.response.ApiResponseDto;
import com.unaldi.authservice.dto.response.PageResponse;
import com.unaldi.authservice.dto.response.PrivilegeResponseDto;
import com.unaldi.authservice.dto.response.PrivilegeStatisticsResponseDto;
import com.unaldi.authservice.service.PrivilegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for privilege management operations.
 * Handles CRUD operations for privileges.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/privileges")
@RequiredArgsConstructor
@Tag(name = "Privilege Management", description = "Privilege management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    /**
     * Creates a new privilege
     *
     * @param request Create privilege request
     * @return Created privilege response
     */
    @PostMapping
    @Operation(
            summary = "Create new privilege",
            description = "Creates a new privilege with specified resource and action"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Privilege created successfully",
                    content = @Content(schema = @Schema(implementation = PrivilegeResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - privilege already exists",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_CREATE')")
    public ResponseEntity<ApiResponseDto<PrivilegeResponseDto>> createPrivilege(@Valid @RequestBody CreatePrivilegeRequestDto request) {
        log.info("Creating new privilege: {}", request.getCode());

        PrivilegeResponseDto response = privilegeService.createPrivilege(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(response, "Privilege created successfully"));
    }

    /**
     * Updates an existing privilege
     *
     * @param id Privilege ID
     * @param request Update privilege request
     * @return Updated privilege response
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update privilege",
            description = "Updates an existing privilege's properties"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privilege updated successfully",
                    content = @Content(schema = @Schema(implementation = PrivilegeResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Privilege not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_UPDATE')")
    public ResponseEntity<ApiResponseDto<PrivilegeResponseDto>> updatePrivilege(
            @Parameter(description = "Privilege ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdatePrivilegeRequestDto request
    ) {
        log.info("Updating privilege with ID: {}", id);

        PrivilegeResponseDto response = privilegeService.updatePrivilege(id, request);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privilege updated successfully"));
    }

    /**
     * Gets a privilege by ID
     *
     * @param id Privilege ID
     * @return Privilege response
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get privilege by ID",
            description = "Retrieves a privilege by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privilege found",
                    content = @Content(schema = @Schema(implementation = PrivilegeResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Privilege not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<PrivilegeResponseDto>> getPrivilegeById(@Parameter(description = "Privilege ID", required = true) @PathVariable Long id) {
        log.debug("Getting privilege by ID: {}", id);

        PrivilegeResponseDto response = privilegeService.getPrivilegeById(id);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privilege retrieved successfully"));
    }

    /**
     * Gets a privilege by code
     *
     * @param code Privilege code
     * @return Privilege response
     */
    @GetMapping("/by-code/{code}")
    @Operation(
            summary = "Get privilege by code",
            description = "Retrieves a privilege by its unique code"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privilege found",
                    content = @Content(schema = @Schema(implementation = PrivilegeResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Privilege not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<PrivilegeResponseDto>> getPrivilegeByCode(@Parameter(description = "Privilege code", required = true) @PathVariable String code) {
        log.debug("Getting privilege by code: {}", code);

        PrivilegeResponseDto response = privilegeService.getPrivilegeByCode(code);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privilege retrieved successfully"));
    }

    /**
     * Gets all privileges with pagination
     *
     * @param page Page number (0-based)
     * @param size Page size
     * @param sort Sort field and direction
     * @return Page of privileges
     */
    @GetMapping
    @Operation(
            summary = "Get all privileges",
            description = "Retrieves all privileges with pagination support"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privileges retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<PageResponse<PrivilegeResponseDto>>> getAllPrivileges(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction", example = "code,asc") @RequestParam(defaultValue = "code,asc") String sort
    ) {
        log.debug("Getting all privileges - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        PageResponse<PrivilegeResponseDto> response = privilegeService.getAllPrivileges(pageable);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privileges retrieved successfully"));
    }

    /**
     * Gets all active privileges
     *
     * @return List of active privileges
     */
    @GetMapping("/active")
    @Operation(
            summary = "Get active privileges",
            description = "Retrieves all active privileges without pagination"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Active privileges retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrivilegeResponseDto.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<List<PrivilegeResponseDto>>> getActivePrivileges() {
        log.debug("Getting all active privileges");

        List<PrivilegeResponseDto> response = privilegeService.getActivePrivileges();

        return ResponseEntity.ok(ApiResponseDto.success(response, "Active privileges retrieved successfully"));
    }

    /**
     * Gets privileges by resource
     *
     * @param resource Resource name
     * @return List of privileges for the resource
     */
    @GetMapping("/by-resource/{resource}")
    @Operation(
            summary = "Get privileges by resource",
            description = "Retrieves all privileges for a specific resource"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privileges retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrivilegeResponseDto.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<List<PrivilegeResponseDto>>> getPrivilegesByResource(@Parameter(description = "Resource name", required = true) @PathVariable String resource) {
        log.debug("Getting privileges for resource: {}", resource);

        List<PrivilegeResponseDto> response = privilegeService.getPrivilegesByResource(resource);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privileges retrieved successfully"));
    }

    /**
     * Gets privileges by action
     *
     * @param action Action name
     * @return List of privileges for the action
     */
    @GetMapping("/by-action/{action}")
    @Operation(
            summary = "Get privileges by action",
            description = "Retrieves all privileges for a specific action"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privileges retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrivilegeResponseDto.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<List<PrivilegeResponseDto>>> getPrivilegesByAction(@Parameter(description = "Action name", required = true) @PathVariable String action) {
        log.debug("Getting privileges for action: {}", action);

        List<PrivilegeResponseDto> response = privilegeService.getPrivilegesByAction(action);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privileges retrieved successfully"));
    }

    /**
     * Searches privileges by code or name
     *
     * @param pattern Search pattern
     * @param page Page number
     * @param size Page size
     * @return Page of matching privileges
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search privileges",
            description = "Searches privileges by code or name pattern with pagination"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<PageResponse<PrivilegeResponseDto>>> searchPrivileges(
            @Parameter(description = "Search pattern", required = true) @RequestParam String pattern,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size
    ) {
        log.debug("Searching privileges with pattern: {}", pattern);

        Pageable pageable = PageRequest.of(page, size, Sort.by("code").ascending());
        PageResponse<PrivilegeResponseDto> response = privilegeService.searchPrivileges(pattern, pageable);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Search completed successfully"));
    }

    /**
     * Deletes a privilege
     *
     * @param id Privilege ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete privilege",
            description = "Soft deletes a privilege (marks as deleted)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privilege deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Privilege not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete system privilege",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_DELETE')")
    public ResponseEntity<ApiResponseDto<Void>> deletePrivilege(@Parameter(description = "Privilege ID", required = true) @PathVariable Long id) {
        log.info("Deleting privilege with ID: {}", id);

        privilegeService.deletePrivilege(id);

        return ResponseEntity.ok(ApiResponseDto.success(null, "Privilege deleted successfully"));
    }

    /**
     * Gets distinct resources
     *
     * @return List of unique resource names
     */
    @GetMapping("/resources")
    @Operation(
            summary = "Get distinct resources",
            description = "Retrieves all unique resource names from privileges"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resources retrieved successfully",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<List<String>>> getDistinctResources() {
        log.debug("Getting distinct resources");

        List<String> response = privilegeService.getDistinctResources();

        return ResponseEntity.ok(ApiResponseDto.success(response, "Resources retrieved successfully"));
    }

    /**
     * Gets distinct actions
     *
     * @return List of unique action names
     */
    @GetMapping("/actions")
    @Operation(
            summary = "Get distinct actions",
            description = "Retrieves all unique action names from privileges"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Actions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<List<String>>> getDistinctActions() {
        log.debug("Getting distinct actions");

        List<String> response = privilegeService.getDistinctActions();

        return ResponseEntity.ok(ApiResponseDto.success(response, "Actions retrieved successfully"));
    }

    /**
     * Gets privilege statistics
     *
     * @return Privilege statistics
     */
    @GetMapping("/statistics")
    @Operation(
            summary = "Get privilege statistics",
            description = "Retrieves statistical information about privileges"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PrivilegeStatisticsResponseDto.class))
            )
    })
    @PreAuthorize("hasAuthority('PRIVILEGE_READ')")
    public ResponseEntity<ApiResponseDto<PrivilegeStatisticsResponseDto>> getPrivilegeStatistics() {
        log.debug("Getting privilege statistics");

        PrivilegeStatisticsResponseDto response = privilegeService.getPrivilegeStatistics();

        return ResponseEntity.ok(ApiResponseDto.success(response, "Statistics retrieved successfully"));
    }

    /**
     * Parses sort parameter string
     *
     * @param sort Sort string (e.g., "code,asc")
     * @return Sort object
     */
    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        }
        return Sort.by(parts[0]);
    }
}