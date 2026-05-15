package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * OpenFeign HTTP client for the external profile/user service.
 * The base URL is resolved from {@code user.service.url} in application properties.
 * {@code dismiss404 = true} means a 404 response is returned as {@code null} rather than
 * throwing a FeignException, so callers can wrap the result in an Optional.
 */
@FeignClient(name = "user-service", url = "${user.service.url}", dismiss404 = true)
public interface UserServiceFeignClient {

    /**
     * Retrieves a user by their institutional email address.
     *
     * @param email the email to look up
     * @return the user data, or {@code null} if not found (404)
     */
    @GetMapping("/api/v1/internal/users/mail/{email}")
    UserDto findByEmail(@PathVariable("email") String email);

    /**
     * Retrieves a user by their unique ID.
     *
     * @param userId the user's unique identifier
     * @return the user data, or {@code null} if not found (404)
     */
    @GetMapping("/api/v1/internal/users/{userId}")
    UserDto findById(@PathVariable("userId") String userId);

    /**
     * Marks a user as email-verified in the profile service.
     *
     * @param userId the ID of the user to verify
     */
    @PatchMapping("/api/v1/internal/users/{userId}/verify")
    void markUserAsVerified(@PathVariable("userId") String userId);

    /**
     * Updates the user's hashed password in the profile service.
     *
     * @param userId the user's ID
     * @param body   a map containing {@code "hashedPassword"} with the new BCrypt hash
     */
    @PatchMapping("/api/v1/internal/users/{userId}/password")
    void updatePassword(@PathVariable("userId") String userId, @RequestBody Map<String, String> body);
}