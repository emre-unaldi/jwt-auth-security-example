package com.unaldi.authservice.security;

import com.unaldi.authservice.dto.response.UserDetailsResponseDto;
import com.unaldi.authservice.feign.UserServiceFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom implementation of UserDetailsService.
 * Retrieves user details from user-service for authentication.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 11.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceFeignClient userServiceFeignClient;

    /**
     * Loads user details by username (email in this case)
     *
     * @param username User's email address
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details for: {}", username);

        try {
            // Get user details from user-service
            UserDetailsResponseDto userDto = userServiceFeignClient.getUserByEmail(username);

            if (userDto == null) {
                throw new UsernameNotFoundException("User not found with email: " + username);
            }

            // Create authorities from roles and privileges
            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            // Add roles with ROLE_ prefix
            if (userDto.getRoles() != null) {
                authorities.addAll(userDto.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet()));
            }

            // Add privileges as authorities
            if (userDto.getPrivileges() != null) {
                authorities.addAll(userDto.getPrivileges().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()));
            }

            // Create Spring Security User object
            return User.builder()
                    .username(userDto.getEmail())
                    .password(userDto.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(userDto.isLocked())
                    .credentialsExpired(false)
                    .disabled(!userDto.isActive())
                    .build();

        } catch (Exception e) {
            log.error("Error loading user details for: {}", username, e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }
}