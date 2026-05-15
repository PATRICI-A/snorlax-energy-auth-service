package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.UserServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Adapter that implements UserServicePort using OpenFeign to call the external profile service.
 */
@Component
@RequiredArgsConstructor
public class UserServiceFeignAdapter implements UserServicePort {

    private final UserServiceFeignClient feignClient;

    /**
     * Finds a user by email via the profile service.
     *
     * @param email the user's institutional email
     * @return the user data, or empty if not found
     */
    @Override
    public Optional<UserDto> findByEmail(String email) {
        return Optional.ofNullable(feignClient.findByEmail(email));
    }

    /**
     * Finds a user by their unique ID via the profile service.
     *
     * @param userId the user's ID
     * @return the user data, or empty if not found
     */
    @Override
    public Optional<UserDto> findById(String userId) {
        return Optional.ofNullable(feignClient.findById(userId));
    }

    /**
     * Marks the user as verified in the profile service.
     *
     * @param userId the ID of the user to verify
     */
    @Override
    public void markUserAsVerified(String userId) {
        feignClient.markUserAsVerified(userId);
    }

    /**
     * Updates the user's hashed password in the profile service.
     *
     * @param userId            the user's ID
     * @param newHashedPassword the new BCrypt-hashed password
     */
    @Override
    public void updatePassword(String userId, String newHashedPassword) {
        feignClient.updatePassword(userId, Map.of("hashedPassword", newHashedPassword));
    }
}