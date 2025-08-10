package com.unaldi.authservice.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * Custom user principal containing user information extracted from JWT token.
 * Used as the principal object in Spring Security authentication.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserPrincipal implements Serializable {

    /**
     * Unique identifier of the user
     */
    private Long userId;

    /**
     * Email address of the user
     */
    private String email;

    /**
     * Set of role names assigned to the user
     */
    private Set<String> roles;

    /**
     * Set of privilege codes assigned to the user
     */
    private Set<String> privileges;

    /**
     * Returns the username (email in this case)
     *
     * @return User email
     */
    public String getUsername() {
        return email;
    }

    @Override
    public String toString() {
        return "CustomUserPrincipal{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", roles=" + roles.size() + " roles" +
                ", privileges=" + privileges.size() + " privileges" +
                '}';
    }
}