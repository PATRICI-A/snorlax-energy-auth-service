package edu.eci.patricia.DOSW_patricia.domain.ports.out;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;

import java.util.Optional;

/**
 * Output port for persisting and retrieving refresh token sessions.
 */
public interface RefreshTokenRepositoryPort {

    /**
     * Persists a refresh token session.
     *
     * @param token the refresh token to save
     * @return the saved refresh token
     */
    RefreshToken save(RefreshToken token);

    /**
     * Finds a session by its refresh token value.
     *
     * @param refreshToken the refresh token string
     * @return the matching session, or empty if not found
     */
    Optional<RefreshToken> findByToken(String refreshToken);

    /**
     * Finds a session by the user's ID.
     *
     * @param userId the user's ID
     * @return the matching session, or empty if not found
     */
    Optional<RefreshToken> findByUserId(String userId);

    /**
     * Deletes all sessions associated with the given user.
     *
     * @param userId the user's ID
     */
    void deleteByUserId(String userId);
}
