package com.unaldi.authservice.controller;

import com.unaldi.authservice.dto.request.AssignPrivilegesToRoleRequestDto;
import com.unaldi.authservice.dto.request.CreateRoleRequestDto;
import com.unaldi.authservice.dto.request.RemovePrivilegesFromRoleRequestDto;
import com.unaldi.authservice.dto.request.UpdateRoleRequestDto;
import com.unaldi.authservice.dto.response.ApiResponseDto;
import com.unaldi.authservice.dto.response.PageResponse;
import com.unaldi.authservice.dto.response.RoleResponseDto;
import com.unaldi.authservice.dto.response.RoleStatisticsResponseDto;
import com.unaldi.authservice.service.RoleService;
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
 * REST controller for role management operations.
 * Handles CRUD operations for roles and role-privilege assignments.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Role management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleService roleService;

    /**
     * Creates a new role
     *
     * @param request Create role request
     * @return Created role response
     */
    @PostMapping
    @Operation(
            summary = "Create new role",
            description = "Creates a new role with optional privilege assignments"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Role created successfully",
                    content = @Content(schema = @Schema(implementation = RoleResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - role already exists",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> createRole(@Valid @RequestBody CreateRoleRequestDto request) {
        log.info("Creating new role: {}", request.getName());

        RoleResponseDto response = roleService.createRole(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(response, "Role created successfully"));
    }

    /**
     * Updates an existing role
     *
     * @param id Role ID
     * @param request Update role request
     * @return Updated role response
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update role",
            description = "Updates an existing role's properties and privileges"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role updated successfully",
                    content = @Content(schema = @Schema(implementation = RoleResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> updateRole(@Parameter(description = "Role ID", required = true) @PathVariable Long id, @Valid @RequestBody UpdateRoleRequestDto request) {
        log.info("Updating role with ID: {}", id);

        RoleResponseDto response = roleService.updateRole(id, request);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Role updated successfully"));
    }

    /**
     * Gets a role by ID
     *
     * @param id Role ID
     * @return Role response
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get role by ID",
            description = "Retrieves a role by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role found",
                    content = @Content(schema = @Schema(implementation = RoleResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> getRoleById(@Parameter(description = "Role ID", required = true) @PathVariable Long id) {
        log.debug("Getting role by ID: {}", id);

        RoleResponseDto response = roleService.getRoleById(id);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Role retrieved successfully"));
    }

    /**
     * Gets a role by name
     *
     * @param name Role name
     * @return Role response
     */
    @GetMapping("/by-name/{name}")
    @Operation(
            summary = "Get role by name",
            description = "Retrieves a role by its unique name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role found",
                    content = @Content(schema = @Schema(implementation = RoleResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> getRoleByName(@Parameter(description = "Role name", required = true) @PathVariable String name) {
        log.debug("Getting role by name: {}", name);

        RoleResponseDto response = roleService.getRoleByName(name);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Role retrieved successfully"));
    }

    /**
     * Gets all roles with pagination
     *
     * @param page Page number (0-based)
     * @param size Page size
     * @param sort Sort field and direction
     * @return Page of roles
     */
    @GetMapping
    @Operation(
            summary = "Get all roles",
            description = "Retrieves all roles with pagination support"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Roles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponseDto<PageResponse<RoleResponseDto>>> getAllRoles(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction", example = "name,asc") @RequestParam(defaultValue = "name,asc") String sort
    ) {
        log.debug("Getting all roles - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        PageResponse<RoleResponseDto> response = roleService.getAllRoles(pageable);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Roles retrieved successfully"));
    }

    /**
     * Gets all active roles
     *
     * @return List of active roles
     */
    @GetMapping("/active")
    @Operation(
            summary = "Get active roles",
            description = "Retrieves all active roles without pagination"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Active roles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RoleResponseDto.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponseDto<List<RoleResponseDto>>> getActiveRoles() {
        log.debug("Getting all active roles");

        List<RoleResponseDto> response = roleService.getActiveRoles();

        return ResponseEntity.ok(ApiResponseDto.success(response, "Active roles retrieved successfully"));
    }

    /**
     * Searches roles by name pattern
     *
     * @param pattern Search pattern
     * @param page Page number
     * @param size Page size
     * @return Page of matching roles
     */
    @GetMapping("/search")
    @Operation(
            summary = "Search roles",
            description = "Searches roles by name pattern with pagination"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponseDto<PageResponse<RoleResponseDto>>> searchRoles(
            @Parameter(description = "Search pattern", required = true) @RequestParam String pattern,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size
    ) {
        log.debug("Searching roles with pattern: {}", pattern);

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        PageResponse<RoleResponseDto> response = roleService.searchRoles(pattern, pageable);

        return ResponseEntity.ok(ApiResponseDto.success(response, "Search completed successfully"));
    }

    /**
     * Deletes a role
     *
     * @param id Role ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete role",
            description = "Soft deletes a role (marks as deleted)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot delete system role",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<ApiResponseDto<Void>> deleteRole(@Parameter(description = "Role ID", required = true) @PathVariable Long id) {
        log.info("Deleting role with ID: {}", id);

        roleService.deleteRole(id);

        return ResponseEntity.ok(ApiResponseDto.success(null, "Role deleted successfully"));
    }

    /**
     * Assigns privileges to a role
     *
     * @param request Assign privileges request
     * @return Updated role response
     */
    @PostMapping("/assign-privileges")
    @Operation(
            summary = "Assign privileges to role",
            description = "Assigns one or more privileges to a role"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privileges assigned successfully",
                    content = @Content(schema = @Schema(implementation = RoleResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role or privilege not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_PRIVILEGE_ASSIGN')")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> assignPrivilegesToRole(@Valid @RequestBody AssignPrivilegesToRoleRequestDto request) {
        log.info("Assigning privileges to role ID: {}", request.getRoleId());

        RoleResponseDto response = roleService.assignPrivilegesToRole(
                request.getRoleId(),
                request.getPrivilegeIds(),
                request.isReplaceExisting()
        );

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privileges assigned successfully"));
    }

    /**
     * Removes privileges from a role
     *
     * @param request Remove privileges request
     * @return Updated role response
     */
    @PostMapping("/remove-privileges")
    @Operation(
            summary = "Remove privileges from role",
            description = "Removes one or more privileges from a role"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Privileges removed successfully",
                    content = @Content(schema = @Schema(implementation = RoleResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_PRIVILEGE_REMOVE')")
    public ResponseEntity<ApiResponseDto<RoleResponseDto>> removePrivilegesFromRole(@Valid @RequestBody RemovePrivilegesFromRoleRequestDto request) {
        log.info("Removing privileges from role ID: {}", request.getRoleId());

        RoleResponseDto response = roleService.removePrivilegesFromRole(
                request.getRoleId(),
                request.getPrivilegeIds()
        );

        return ResponseEntity.ok(ApiResponseDto.success(response, "Privileges removed successfully"));
    }

    /**
     * Gets role statistics
     *
     * @return Role statistics
     */
    @GetMapping("/statistics")
    @Operation(
            summary = "Get role statistics",
            description = "Retrieves statistical information about roles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RoleStatisticsResponseDto.class))
            )
    })
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<ApiResponseDto<RoleStatisticsResponseDto>> getRoleStatistics() {
        log.debug("Getting role statistics");

        RoleStatisticsResponseDto response = roleService.getRoleStatistics();

        return ResponseEntity.ok(ApiResponseDto.success(response, "Statistics retrieved successfully"));
    }

    /**
     * Parses sort parameter string
     *
     * @param sort Sort string (e.g., "name,asc")
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