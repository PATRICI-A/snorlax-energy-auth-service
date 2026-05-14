package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when a JWT or refresh token has passed its expiry time.
 * Mapped to HTTP 401 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class TokenExpiredException extends RuntimeException {

    /** @param message human-readable message indicating the token has expired */
    public TokenExpiredException(String message) {
        super(message);
    }
}
