package com.unaldi.authservice.service;

import com.unaldi.authservice.dto.request.CreatePrivilegeRequestDto;
import com.unaldi.authservice.dto.request.UpdatePrivilegeRequestDto;
import com.unaldi.authservice.dto.response.PageResponse;
import com.unaldi.authservice.dto.response.PrivilegeResponseDto;
import com.unaldi.authservice.dto.response.PrivilegeStatisticsResponseDto;
import com.unaldi.authservice.model.Privilege;
import com.unaldi.authservice.exception.ResourceNotFoundException;
import com.unaldi.authservice.exception.ValidationException;
import com.unaldi.authservice.mapper.PrivilegeMapper;
import com.unaldi.authservice.repository.PrivilegeRepository;
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
 * Service implementation for privilege management operations.
 * Handles CRUD operations for privileges.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeMapper privilegeMapper;

    /**
     * Creates a new privilege
     *
     * @param request Create privilege request
     * @return Created privilege response
     * @throws ValidationException if validation fails
     */
    @Transactional
    public PrivilegeResponseDto createPrivilege(CreatePrivilegeRequestDto request) {
        log.info("Creating new privilege: {}", request.getCode());

        // Check if privilege already exists
        if (privilegeRepository.existsByCodeAndNotDeleted(request.getCode().toUpperCase())) {
            throw new ValidationException("Privilege with code '" + request.getCode() + "' already exists");
        }

        // Validate code format
        String generatedCode = Privilege.generateCode(request.getResource(), request.getAction());
        if (!request.getCode().equalsIgnoreCase(generatedCode)) {
            log.warn("Privilege code '{}' doesn't match pattern '{}'", request.getCode(), generatedCode);
        }

        // Create privilege entity
        Privilege privilege = Privilege.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .description(request.getDescription())
                .resource(request.getResource().toUpperCase())
                .action(request.getAction().toUpperCase())
                .active(request.isActive())
                .build();

        // Save privilege
        Privilege savedPrivilege = privilegeRepository.save(privilege);
        log.info("Successfully created privilege: {}", savedPrivilege.getCode());

        return privilegeMapper.toPrivilegeResponse(savedPrivilege);
    }

    /**
     * Updates an existing privilege
     *
     * @param id Privilege ID
     * @param request Update privilege request
     * @return Updated privilege response
     * @throws ResourceNotFoundException if privilege not found
     */
    @Transactional
    public PrivilegeResponseDto updatePrivilege(Long id, UpdatePrivilegeRequestDto request) {
        log.info("Updating privilege with ID: {}", id);

        Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Privilege not found with ID: " + id));

        if (privilege.isDeleted()) {
            throw new ResourceNotFoundException("Privilege has been deleted");
        }

        if (privilege.isSystem()) {
            throw new ValidationException("System privileges cannot be modified");
        }

        // Update fields if provided
        if (request.getName() != null) {
            privilege.setName(request.getName());
        }

        if (request.getDescription() != null) {
            privilege.setDescription(request.getDescription());
        }

        if (request.getActive() != null) {
            privilege.setActive(request.getActive());
        }

        // Save updated privilege
        Privilege updatedPrivilege = privilegeRepository.save(privilege);
        log.info("Successfully updated privilege: {}", updatedPrivilege.getCode());

        return privilegeMapper.toPrivilegeResponse(updatedPrivilege);
    }

    /**
     * Gets a privilege by ID
     *
     * @param id Privilege ID
     * @return Privilege response
     * @throws ResourceNotFoundException if privilege not found
     */
    @Transactional(readOnly = true)
    public PrivilegeResponseDto getPrivilegeById(Long id) {
        log.debug("Getting privilege by ID: {}", id);

        Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Privilege not found with ID: " + id));

        if (privilege.isDeleted()) {
            throw new ResourceNotFoundException("Privilege has been deleted");
        }

        return privilegeMapper.toPrivilegeResponse(privilege);
    }

    /**
     * Gets a privilege by code
     *
     * @param code Privilege code
     * @return Privilege response
     * @throws ResourceNotFoundException if privilege not found
     */
    @Transactional(readOnly = true)
    public PrivilegeResponseDto getPrivilegeByCode(String code) {
        log.debug("Getting privilege by code: {}", code);

        Privilege privilege = privilegeRepository.findByCodeAndNotDeleted(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Privilege not found with code: " + code));

        return privilegeMapper.toPrivilegeResponse(privilege);
    }

    /**
     * Gets all privileges with pagination
     *
     * @param pageable Pagination parameters
     * @return Page of privilege responses
     */
    @Transactional(readOnly = true)
    public PageResponse<PrivilegeResponseDto> getAllPrivileges(Pageable pageable) {
        log.debug("Getting all privileges with pagination");

        Page<Privilege> privilegePage = privilegeRepository.findAllNotDeleted(pageable);

        return PageResponse.<PrivilegeResponseDto>builder()
                .content(privilegePage.getContent().stream()
                        .map(privilegeMapper::toPrivilegeResponse)
                        .collect(Collectors.toList()))
                .pageNumber(privilegePage.getNumber())
                .pageSize(privilegePage.getSize())
                .totalElements(privilegePage.getTotalElements())
                .totalPages(privilegePage.getTotalPages())
                .last(privilegePage.isLast())
                .first(privilegePage.isFirst())
                .numberOfElements(privilegePage.getNumberOfElements())
                .empty(privilegePage.isEmpty())
                .build();
    }

    /**
     * Gets all active privileges
     *
     * @return List of active privilege responses
     */
    @Transactional(readOnly = true)
    public List<PrivilegeResponseDto> getActivePrivileges() {
        log.debug("Getting all active privileges");

        List<Privilege> activePrivileges = privilegeRepository.findAllActivePrivileges();

        return activePrivileges.stream()
                .map(privilegeMapper::toPrivilegeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets privileges by resource
     *
     * @param resource Resource name
     * @return List of privilege responses for the resource
     */
    @Transactional(readOnly = true)
    public List<PrivilegeResponseDto> getPrivilegesByResource(String resource) {
        log.debug("Getting privileges for resource: {}", resource);

        List<Privilege> privileges = privilegeRepository.findByResourceAndNotDeleted(resource.toUpperCase());

        return privileges.stream()
                .map(privilegeMapper::toPrivilegeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets privileges by action
     *
     * @param action Action name
     * @return List of privilege responses for the action
     */
    @Transactional(readOnly = true)
    public List<PrivilegeResponseDto> getPrivilegesByAction(String action) {
        log.debug("Getting privileges for action: {}", action);

        List<Privilege> privileges = privilegeRepository.findByActionAndNotDeleted(action.toUpperCase());

        return privileges.stream()
                .map(privilegeMapper::toPrivilegeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Searches privileges by code or name
     *
     * @param pattern Search pattern
     * @param pageable Pagination parameters
     * @return Page of matching privilege responses
     */
    @Transactional(readOnly = true)
    public PageResponse<PrivilegeResponseDto> searchPrivileges(String pattern, Pageable pageable) {
        log.debug("Searching privileges with pattern: {}", pattern);

        Page<Privilege> privilegePage = privilegeRepository.searchByCodeOrName(pattern, pageable);

        return PageResponse.<PrivilegeResponseDto>builder()
                .content(privilegePage.getContent().stream()
                        .map(privilegeMapper::toPrivilegeResponse)
                        .collect(Collectors.toList()))
                .pageNumber(privilegePage.getNumber())
                .pageSize(privilegePage.getSize())
                .totalElements(privilegePage.getTotalElements())
                .totalPages(privilegePage.getTotalPages())
                .last(privilegePage.isLast())
                .first(privilegePage.isFirst())
                .numberOfElements(privilegePage.getNumberOfElements())
                .empty(privilegePage.isEmpty())
                .build();
    }

    /**
     * Deletes a privilege (soft delete)
     *
     * @param id Privilege ID
     * @throws ResourceNotFoundException if privilege not found
     */
    @Transactional
    public void deletePrivilege(Long id) {
        log.info("Deleting privilege with ID: {}", id);

        Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Privilege not found with ID: " + id));

        if (privilege.isDeleted()) {
            throw new ResourceNotFoundException("Privilege has already been deleted");
        }

        if (privilege.isSystem()) {
            throw new ValidationException("System privileges cannot be deleted");
        }

        // Get current user email for audit
        String deletedBy = getCurrentUserEmail();

        // Soft delete
        privilegeRepository.softDelete(id, deletedBy);

        log.info("Successfully deleted privilege: {}", privilege.getCode());
    }

    /**
     * Gets distinct resources
     *
     * @return List of unique resource names
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctResources() {
        log.debug("Getting distinct resources");
        return privilegeRepository.findDistinctResources();
    }

    /**
     * Gets distinct actions
     *
     * @return List of unique action names
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctActions() {
        log.debug("Getting distinct actions");
        return privilegeRepository.findDistinctActions();
    }

    /**
     * Gets privilege statistics
     *
     * @return Privilege statistics response
     */
    @Transactional(readOnly = true)
    public PrivilegeStatisticsResponseDto getPrivilegeStatistics() {
        log.debug("Getting privilege statistics");

        long totalPrivileges = privilegeRepository.count();
        long activePrivileges = privilegeRepository.findAllActivePrivileges().size();
        long systemPrivileges = privilegeRepository.findSystemPrivileges().size();
        long customPrivileges = totalPrivileges - systemPrivileges;
        long unassignedPrivileges = privilegeRepository.findUnassignedPrivileges().size();

        Set<String> resources = new HashSet<>(privilegeRepository.findDistinctResources());
        Set<String> actions = new HashSet<>(privilegeRepository.findDistinctActions());

        return PrivilegeStatisticsResponseDto.builder()
                .totalPrivileges(totalPrivileges)
                .activePrivileges(activePrivileges)
                .systemPrivileges(systemPrivileges)
                .customPrivileges(customPrivileges)
                .unassignedPrivileges(unassignedPrivileges)
                .resources(resources)
                .actions(actions)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Validates privilege codes
     *
     * @param privilegeCodes Set of privilege codes to validate
     * @return Set of valid privilege codes
     */
    @Transactional(readOnly = true)
    public Set<String> validatePrivilegeCodes(Set<String> privilegeCodes) {
        log.debug("Validating {} privilege codes", privilegeCodes.size());

        Set<String> upperCaseCodes = privilegeCodes.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        List<Privilege> validPrivileges = privilegeRepository.findByCodeInAndNotDeleted(upperCaseCodes);

        return validPrivileges.stream()
                .map(Privilege::getCode)
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