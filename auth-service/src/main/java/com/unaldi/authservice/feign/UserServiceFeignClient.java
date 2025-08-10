package com.unaldi.authservice.feign;

import com.unaldi.authservice.dto.response.UserDetailsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communication with user-service.
 * Handles user-related operations required for authentication.
 *
 * @author Emre Ünaldı
 * @version 1.0
 * @since 10.08.2025
 */
@FeignClient(name = "user-service", url = "http://localhost:8082", path = "/api/v1/internal/users")
public interface UserServiceFeignClient {

    /**
     * Gets user details by email
     *
     * @param email User email
     * @return User details
     */
    @GetMapping("/by-email")
    UserDetailsResponseDto getUserByEmail(@RequestParam("email") String email);

    /**
     * Gets user details by ID
     *
     * @param userId User ID
     * @return User details
     */
    @GetMapping("/{userId}")
    UserDetailsResponseDto getUserById(@PathVariable("userId") Long userId);

    /**
     * Validates user password
     *
     * @param userId User ID
     * @param password Password to validate
     * @return true if password is valid
     */
    @GetMapping("/{userId}/validate-password")
    boolean validateUserPassword(@PathVariable("userId") Long userId, @RequestParam("password") String password);

}