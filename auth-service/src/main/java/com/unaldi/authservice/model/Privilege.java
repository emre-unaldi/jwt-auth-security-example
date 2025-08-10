package com.unaldi.authservice.model;

import com.unaldi.authservice.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a privilege/permission in the system.
 * Privileges define specific actions that can be performed.
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Entity
@Table(name = "privileges",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_privilege_code", columnList = "code"),
                @Index(name = "idx_privilege_deleted", columnList = "is_deleted")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege extends BaseEntity {

    /**
     * Unique code identifying the privilege (e.g., USER_READ, USER_WRITE)
     */
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    /**
     * Human-readable name of the privilege
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Detailed description of what this privilege allows
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Resource this privilege applies to (e.g., USER, PRODUCT, ORDER)
     */
    @Column(name = "resource", length = 50)
    private String resource;

    /**
     * Action this privilege allows (e.g., READ, WRITE, DELETE)
     */
    @Column(name = "action", length = 50)
    private String action;

    /**
     * Flag indicating if this is a system privilege that cannot be deleted
     */
    @Column(name = "is_system", nullable = false)
    @Builder.Default
    private boolean system = false;

    /**
     * Flag indicating if the privilege is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Roles that have this privilege
     */
    @ManyToMany(mappedBy = "privileges", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Generates a privilege code from resource and action
     *
     * @param resource The resource name
     * @param action The action name
     * @return The generated privilege code
     */
    public static String generateCode(String resource, String action) {
        return String.format("%s_%s", resource.toUpperCase(), action.toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Privilege privilege)) return false;
        return getId() != null && getId().equals(privilege.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Privilege{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
