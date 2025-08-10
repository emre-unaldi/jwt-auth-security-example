package com.unaldi.authservice.service;

import com.unaldi.authservice.dto.request.CreateRoleRequestDto;
import com.unaldi.authservice.dto.request.UpdateRoleRequestDto;
import com.unaldi.authservice.dto.response.PageResponse;
import com.unaldi.authservice.dto.response.RoleResponseDto;
import com.unaldi.authservice.dto.response.RoleStatisticsResponseDto;
import com.unaldi.authservice.model.Privilege;
import com.unaldi.authservice.model.Role;
import com.unaldi.authservice.exception.ResourceNotFoundException;
import com.unaldi.authservice.exception.ValidationException;
import com.unaldi.authservice.mapper.RoleMapper;
import com.unaldi.authservice.repository.PrivilegeRepository;
import com.unaldi.authservice.repository.RoleRepository;
import com.unaldi.authservice.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for role management operations.
 * Handles CRUD operations for roles and role-privilege assignments.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RoleMapper roleMapper;

    /**
     * Creates a new role
     *
     * @param request Create role request
     * @return Created role response
     * @throws ValidationException if validation fails
     */
    @Transactional
    public RoleResponseDto createRole(CreateRoleRequestDto request) {
        log.info("Creating new role: {}", request.getName());

        // Check if role already exists
        if (roleRepository.existsByNameAndNotDeleted(request.getName())) {
            throw new ValidationException("Role with name '" + request.getName() + "' already exists");
        }

        // Create role entity
        Role role = Role.builder()
                .name(request.getName().toUpperCase())
                .description(request.getDescription())
                .active(request.isActive())
                .build();

        // Add privileges if provided
        if (request.getPrivilegeIds() != null && !request.getPrivilegeIds().isEmpty()) {
            Set<Privilege> privileges = new HashSet<>(
                    privilegeRepository.findAllById(request.getPrivilegeIds())
            );

            if (privileges.size() != request.getPrivilegeIds().size()) {
                throw new ValidationException("Some privilege IDs are invalid");
            }

            role.setPrivileges(privileges);
        }

        // Save role
        Role savedRole = roleRepository.save(role);
        log.info("Successfully created role: {}", savedRole.getName());

        return roleMapper.toRoleResponse(savedRole);
    }

    /**
     * Updates an existing role
     *
     * @param id Role ID
     * @param request Update role request
     * @return Updated role response
     * @throws ResourceNotFoundException if role not found
     */
    @Transactional
    public RoleResponseDto updateRole(Long id, UpdateRoleRequestDto request) {
        log.info("Updating role with ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if (role.isDeleted()) {
            throw new ResourceNotFoundException("Role has been deleted");
        }

        if (role.isSystem()) {
            throw new ValidationException("System roles cannot be modified");
        }

        // Update fields if provided
        if (request.getName() != null) {
            // Check if new name already exists
            if (!role.getName().equals(request.getName()) &&
                    roleRepository.existsByNameAndNotDeleted(request.getName())) {
                throw new ValidationException("Role with name '" + request.getName() + "' already exists");
            }
            role.setName(request.getName().toUpperCase());
        }

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        if (request.getActive() != null) {
            role.setActive(request.getActive());
        }

        // Update privileges if provided
        if (request.getPrivilegeIds() != null) {
            Set<Privilege> privileges = new HashSet<>(
                    privilegeRepository.findAllById(request.getPrivilegeIds())
            );

            if (privileges.size() != request.getPrivilegeIds().size()) {
                throw new ValidationException("Some privilege IDs are invalid");
            }

            role.setPrivileges(privileges);
        }

        // Save updated role
        Role updatedRole = roleRepository.save(role);
        log.info("Successfully updated role: {}", updatedRole.getName());

        return roleMapper.toRoleResponse(updatedRole);
    }

    /**
     * Gets a role by ID
     *
     * @param id Role ID
     * @return Role response
     * @throws ResourceNotFoundException if role not found
     */
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleById(Long id) {
        log.debug("Getting role by ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if (role.isDeleted()) {
            throw new ResourceNotFoundException("Role has been deleted");
        }

        return roleMapper.toRoleResponse(role);
    }

    /**
     * Gets a role by name
     *
     * @param name Role name
     * @return Role response
     * @throws ResourceNotFoundException if role not found
     */
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleByName(String name) {
        log.debug("Getting role by name: {}", name);

        Role role = roleRepository.findByNameAndNotDeleted(name.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));

        return roleMapper.toRoleResponse(role);
    }

    /**
     * Gets all roles with pagination
     *
     * @param pageable Pagination parameters
     * @return Page of role responses
     */
    @Transactional(readOnly = true)
    public PageResponse<RoleResponseDto> getAllRoles(Pageable pageable) {
        log.debug("Getting all roles with pagination");

        Page<Role> rolePage = roleRepository.findAllNotDeleted(pageable);

        return PageResponse.<RoleResponseDto>builder()
                .content(rolePage.getContent().stream()
                        .map(roleMapper::toRoleResponse)
                        .collect(Collectors.toList()))
                .pageNumber(rolePage.getNumber())
                .pageSize(rolePage.getSize())
                .totalElements(rolePage.getTotalElements())
                .totalPages(rolePage.getTotalPages())
                .last(rolePage.isLast())
                .first(rolePage.isFirst())
                .numberOfElements(rolePage.getNumberOfElements())
                .empty(rolePage.isEmpty())
                .build();
    }

    /**
     * Gets all active roles
     *
     * @return List of active role responses
     */
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getActiveRoles() {
        log.debug("Getting all active roles");

        List<Role> activeRoles = roleRepository.findAllActiveRoles();

        return activeRoles.stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    /**
     * Searches roles by name pattern
     *
     * @param pattern Search pattern
     * @param pageable Pagination parameters
     * @return Page of matching role responses
     */
    @Transactional(readOnly = true)
    public PageResponse<RoleResponseDto> searchRoles(String pattern, Pageable pageable) {
        log.debug("Searching roles with pattern: {}", pattern);

        Page<Role> rolePage = roleRepository.searchByName(pattern, pageable);

        return PageResponse.<RoleResponseDto>builder()
                .content(rolePage.getContent().stream()
                        .map(roleMapper::toRoleResponse)
                        .collect(Collectors.toList()))
                .pageNumber(rolePage.getNumber())
                .pageSize(rolePage.getSize())
                .totalElements(rolePage.getTotalElements())
                .totalPages(rolePage.getTotalPages())
                .last(rolePage.isLast())
                .first(rolePage.isFirst())
                .numberOfElements(rolePage.getNumberOfElements())
                .empty(rolePage.isEmpty())
                .build();
    }

    /**
     * Deletes a role (soft delete)
     *
     * @param id Role ID
     * @throws ResourceNotFoundException if role not found
     */
    @Transactional
    public void deleteRole(Long id) {
        log.info("Deleting role with ID: {}", id);

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if (role.isDeleted()) {
            throw new ResourceNotFoundException("Role has already been deleted");
        }

        if (role.isSystem()) {
            throw new ValidationException("System roles cannot be deleted");
        }

        // Get current user email for audit
        String deletedBy = getCurrentUserEmail();

        // Soft delete
        roleRepository.softDelete(id, deletedBy);

        log.info("Successfully deleted role: {}", role.getName());
    }

    /**
     * Assigns privileges to a role
     *
     * @param roleId Role ID
     * @param privilegeIds Set of privilege IDs
     * @param replaceExisting Whether to replace existing privileges
     * @return Updated role response
     */
    @Transactional
    public RoleResponseDto assignPrivilegesToRole(Long roleId, Set<Long> privilegeIds, boolean replaceExisting) {
        log.info("Assigning privileges to role ID: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        if (role.isDeleted()) {
            throw new ResourceNotFoundException("Role has been deleted");
        }

        Set<Privilege> privileges = new HashSet<>(privilegeRepository.findAllById(privilegeIds));

        if (privileges.size() != privilegeIds.size()) {
            throw new ValidationException("Some privilege IDs are invalid");
        }

        if (replaceExisting) {
            role.setPrivileges(privileges);
        } else {
            role.getPrivileges().addAll(privileges);
        }

        Role updatedRole = roleRepository.save(role);
        log.info("Successfully assigned {} privileges to role: {}", privileges.size(), role.getName());

        return roleMapper.toRoleResponse(updatedRole);
    }

    /**
     * Removes privileges from a role
     *
     * @param roleId Role ID
     * @param privilegeIds Set of privilege IDs to remove
     * @return Updated role response
     */
    @Transactional
    public RoleResponseDto removePrivilegesFromRole(Long roleId, Set<Long> privilegeIds) {
        log.info("Removing privileges from role ID: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        if (role.isDeleted()) {
            throw new ResourceNotFoundException("Role has been deleted");
        }

        Set<Privilege> privilegesToRemove = new HashSet<>(privilegeRepository.findAllById(privilegeIds));

        role.getPrivileges().removeAll(privilegesToRemove);

        Role updatedRole = roleRepository.save(role);
        log.info("Successfully removed {} privileges from role: {}", privilegesToRemove.size(), role.getName());

        return roleMapper.toRoleResponse(updatedRole);
    }

    /**
     * Gets role statistics
     *
     * @return Role statistics response
     */
    @Transactional(readOnly = true)
    public RoleStatisticsResponseDto getRoleStatistics() {
        log.debug("Getting role statistics");

        List<Role> allRoles = roleRepository.findAllActiveRoles();

        long totalRoles = roleRepository.count();
        long activeRoles = roleRepository.countActiveRoles();
        long systemRoles = roleRepository.findSystemRoles().size();
        long customRoles = totalRoles - systemRoles;

        double avgPrivilegesPerRole = allRoles.stream()
                .mapToInt(role -> role.getPrivileges().size())
                .average()
                .orElse(0.0);

        return RoleStatisticsResponseDto.builder()
                .totalRoles(totalRoles)
                .activeRoles(activeRoles)
                .systemRoles(systemRoles)
                .customRoles(customRoles)
                .averagePrivilegesPerRole(avgPrivilegesPerRole)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Validates role names
     *
     * @param roleNames Set of role names to validate
     * @return Set of valid role names
     */
    @Transactional(readOnly = true)
    public Set<String> validateRoleNames(Set<String> roleNames) {
        log.debug("Validating {} role names", roleNames.size());

        Set<String> upperCaseNames = roleNames.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        List<Role> validRoles = roleRepository.findByNameInAndNotDeleted(upperCaseNames);

        return validRoles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the current authenticated user's email
     *
     * @return User email or "system" if not authenticated
     */
    private String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) authentication.getPrincipal()).getEmail();
        }
        return "system";
    }
}