package com.unaldi.authservice.model;

import com.unaldi.authservice.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a role in the system.
 * Roles can have multiple privileges associated with them.
 *
 * @author Emre Ünaldı
 * @since 10.08.2025
 * @version 1.0
 */
@Entity
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_role_name", columnList = "name"),
                @Index(name = "idx_role_deleted", columnList = "is_deleted")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    /**
     * Unique name of the role (e.g., USER, ADMIN, SALON)
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Human-readable description of the role
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Flag indicating if this is a system role that cannot be deleted
     */
    @Column(name = "is_system", nullable = false)
    @Builder.Default
    private boolean system = false;

    /**
     * Flag indicating if the role is active
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Privileges associated with this role
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_privileges",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"),
            indexes = {
                    @Index(name = "idx_role_privilege_role", columnList = "role_id"),
                    @Index(name = "idx_role_privilege_privilege", columnList = "privilege_id")
            }
    )
    @Builder.Default
    private Set<Privilege> privileges = new HashSet<>();

    /**
     * Adds a privilege to this role
     *
     * @param privilege The privilege to add
     */
    public void addPrivilege(Privilege privilege) {
        if (privilege != null) {
            this.privileges.add(privilege);
        }
    }

    /**
     * Removes a privilege from this role
     *
     * @param privilege The privilege to remove
     */
    public void removePrivilege(Privilege privilege) {
        if (privilege != null) {
            this.privileges.remove(privilege);
        }
    }

    /**
     * Clears all privileges from this role
     */
    public void clearPrivileges() {
        this.privileges.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return getId() != null && getId().equals(role.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
