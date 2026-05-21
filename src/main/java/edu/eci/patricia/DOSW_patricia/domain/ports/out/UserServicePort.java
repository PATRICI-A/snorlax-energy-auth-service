package edu.eci.patricia.DOSW_patricia.domain.ports.out;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;

import java.util.Optional;

/**
 * Output port for communicating with the external user/profile service.
 */
public interface UserServicePort {

    /**
     * Finds a user by their institutional email address.
     *
     * @param email the user's email
     * @return the user data, or empty if not found
     */
    Optional<UserDto> findByEmail(String email);

    /**
     * Finds a user by their unique ID.
     *
     * @param userId the user's ID
     * @return the user data, or empty if not found
     */
    Optional<UserDto> findById(String userId);

    /**
     * Marks the user's account as verified in the profile service.
     *
     * @param userId the ID of the user to verify
     */
    void markUserAsVerified(String userId);

    /**
     * Sends the new plain-text password to the profile service, which handles hashing.
     *
     * @param userId      the user's ID
     * @param newPassword the new plain-text password
     */
    void updatePassword(String userId, String newPassword);
}
