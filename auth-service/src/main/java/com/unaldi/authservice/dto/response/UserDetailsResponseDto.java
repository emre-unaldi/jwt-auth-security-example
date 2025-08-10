package com.unaldi.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for user details received from user-service.
 * Contains user information needed for authentication and authorization.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponseDto {

    /**
     * User ID
     */
    private Long id;

    /**
     * User email (unique identifier)
     */
    private String email;

    /**
     * Encrypted password
     */
    private String password;

    /**
     * User's first name
     */
    private String firstName;

    /**
     * User's last name
     */
    private String lastName;

    /**
     * Whether the user account is active
     */
    private boolean active;

    /**
     * Whether the user account is locked
     */
    private boolean locked;

    /**
     * Set of role names assigned to the user
     */
    private Set<String> roles;

    /**
     * Set of privilege codes derived from user's roles
     */
    private Set<String> privileges;
}