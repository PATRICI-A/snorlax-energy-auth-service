package edu.eci.patricia.DOSW_patricia.domain.ports.in;

/**
 * Input port for logging out a user by invalidating their session.
 */
public interface LogoutPort {

    /**
     * Invalidates the session associated with the given JWT access token.
     *
     * @param token the JWT access token from the Authorization header
     */
    void logout(String token);
}
