package com.unaldi.authservice.repository;

import com.unaldi.authservice.model.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Privilege entity operations.
 * Provides CRUD operations and custom queries for privilege management.
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    /**
     * Finds a privilege by its code, excluding soft-deleted records
     *
     * @param code The privilege code
     * @return Optional containing the privilege if found
     */
    @Query("SELECT p FROM Privilege p WHERE p.code = :code AND p.deleted = false")
    Optional<Privilege> findByCodeAndNotDeleted(@Param("code") String code);

    /**
     * Finds all active privileges that are not deleted
     *
     * @return List of active privileges
     */
    @Query("SELECT p FROM Privilege p WHERE p.active = true AND p.deleted = false")
    List<Privilege> findAllActivePrivileges();

    /**
     * Finds all privileges with pagination, excluding soft-deleted records
     *
     * @param pageable Pagination information
     * @return Page of privileges
     */
    @Query("SELECT p FROM Privilege p WHERE p.deleted = false")
    Page<Privilege> findAllNotDeleted(Pageable pageable);

    /**
     * Finds privileges by multiple codes
     *
     * @param codes Set of privilege codes
     * @return List of privileges
     */
    @Query("SELECT p FROM Privilege p WHERE p.code IN :codes AND p.deleted = false")
    List<Privilege> findByCodeInAndNotDeleted(@Param("codes") Set<String> codes);

    /**
     * Finds privileges by resource
     *
     * @param resource The resource name
     * @return List of privileges for the resource
     */
    @Query("SELECT p FROM Privilege p WHERE p.resource = :resource AND p.deleted = false")
    List<Privilege> findByResourceAndNotDeleted(@Param("resource") String resource);

    /**
     * Finds privileges by action
     *
     * @param action The action name
     * @return List of privileges for the action
     */
    @Query("SELECT p FROM Privilege p WHERE p.action = :action AND p.deleted = false")
    List<Privilege> findByActionAndNotDeleted(@Param("action") String action);

    /**
     * Finds privileges by resource and action
     *
     * @param resource The resource name
     * @param action The action name
     * @return List of matching privileges
     */
    @Query("SELECT p FROM Privilege p WHERE p.resource = :resource AND p.action = :action AND p.deleted = false")
    List<Privilege> findByResourceAndActionAndNotDeleted(@Param("resource") String resource, @Param("action") String action);

    /**
     * Checks if a privilege exists with the given code
     *
     * @param code The privilege code
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(p) > 0 FROM Privilege p WHERE p.code = :code AND p.deleted = false")
    boolean existsByCodeAndNotDeleted(@Param("code") String code);

    /**
     * Soft deletes a privilege by marking it as deleted
     *
     * @param id The privilege ID
     * @param deletedBy Email of the user performing deletion
     */
    @Modifying
    @Query("UPDATE Privilege p SET p.deleted = true, p.deletedDate = CURRENT_TIMESTAMP, p.deletedBy = :deletedBy WHERE p.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletedBy") String deletedBy);

    /**
     * Finds system privileges that cannot be deleted
     *
     * @return List of system privileges
     */
    @Query("SELECT p FROM Privilege p WHERE p.system = true AND p.deleted = false")
    List<Privilege> findSystemPrivileges();

    /**
     * Searches privileges by code or name pattern
     *
     * @param pattern The search pattern
     * @param pageable Pagination information
     * @return Page of matching privileges
     */
    @Query("SELECT p FROM Privilege p WHERE (LOWER(p.code) LIKE LOWER(CONCAT('%', :pattern, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', :pattern, '%'))) AND p.deleted = false")
    Page<Privilege> searchByCodeOrName(@Param("pattern") String pattern, Pageable pageable);

    /**
     * Gets distinct resources from privileges
     *
     * @return List of unique resource names
     */
    @Query("SELECT DISTINCT p.resource FROM Privilege p WHERE p.deleted = false ORDER BY p.resource")
    List<String> findDistinctResources();

    /**
     * Gets distinct actions from privileges
     *
     * @return List of unique action names
     */
    @Query("SELECT DISTINCT p.action FROM Privilege p WHERE p.deleted = false ORDER BY p.action")
    List<String> findDistinctActions();

    /**
     * Counts privileges by resource
     *
     * @param resource The resource name
     * @return Count of privileges for the resource
     */
    @Query("SELECT COUNT(p) FROM Privilege p WHERE p.resource = :resource AND p.deleted = false")
    long countByResource(@Param("resource") String resource);

    /**
     * Finds privileges not assigned to any role
     *
     * @return List of unassigned privileges
     */
    @Query("SELECT p FROM Privilege p WHERE p.deleted = false AND NOT EXISTS (SELECT r FROM p.roles r WHERE r.deleted = false)")
    List<Privilege> findUnassignedPrivileges();

}
