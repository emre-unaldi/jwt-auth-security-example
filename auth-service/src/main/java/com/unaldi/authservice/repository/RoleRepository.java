package com.unaldi.authservice.repository;

import com.unaldi.authservice.model.Role;
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
 * Repository interface for Role entity operations.
 * Provides CRUD operations and custom queries for role management.
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name, excluding soft-deleted records
     *
     * @param name The role name
     * @return Optional containing the role if found
     */
    @Query("SELECT r FROM Role r WHERE r.name = :name AND r.deleted = false")
    Optional<Role> findByNameAndNotDeleted(@Param("name") String name);

    /**
     * Finds all active roles that are not deleted
     *
     * @return List of active roles
     */
    @Query("SELECT r FROM Role r WHERE r.active = true AND r.deleted = false")
    List<Role> findAllActiveRoles();

    /**
     * Finds all roles with pagination, excluding soft-deleted records
     *
     * @param pageable Pagination information
     * @return Page of roles
     */
    @Query("SELECT r FROM Role r WHERE r.deleted = false")
    Page<Role> findAllNotDeleted(Pageable pageable);

    /**
     * Finds roles by multiple names
     *
     * @param names Set of role names
     * @return List of roles
     */
    @Query("SELECT r FROM Role r WHERE r.name IN :names AND r.deleted = false")
    List<Role> findByNameInAndNotDeleted(@Param("names") Set<String> names);

    /**
     * Checks if a role exists with the given name
     *
     * @param name The role name
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(r) > 0 FROM Role r WHERE r.name = :name AND r.deleted = false")
    boolean existsByNameAndNotDeleted(@Param("name") String name);

    /**
     * Finds roles that have a specific privilege
     *
     * @param privilegeId The privilege ID
     * @return List of roles with the privilege
     */
    @Query("SELECT r FROM Role r JOIN r.privileges p WHERE p.id = :privilegeId AND r.deleted = false")
    List<Role> findByPrivilegeId(@Param("privilegeId") Long privilegeId);

    /**
     * Soft deletes a role by marking it as deleted
     *
     * @param id The role ID
     * @param deletedBy Email of the user performing deletion
     */
    @Modifying
    @Query("UPDATE Role r SET r.deleted = true, r.deletedDate = CURRENT_TIMESTAMP, r.deletedBy = :deletedBy WHERE r.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletedBy") String deletedBy);

    /**
     * Finds system roles that cannot be deleted
     *
     * @return List of system roles
     */
    @Query("SELECT r FROM Role r WHERE r.system = true AND r.deleted = false")
    List<Role> findSystemRoles();

    /**
     * Searches roles by name pattern
     *
     * @param pattern The search pattern
     * @param pageable Pagination information
     * @return Page of matching roles
     */
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :pattern, '%')) AND r.deleted = false")
    Page<Role> searchByName(@Param("pattern") String pattern, Pageable pageable);

    /**
     * Counts active roles
     *
     * @return Count of active roles
     */
    @Query("SELECT COUNT(r) FROM Role r WHERE r.active = true AND r.deleted = false")
    long countActiveRoles();

}
