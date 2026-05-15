package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when a JWT token fails signature verification or cannot be parsed.
 * Mapped to HTTP 401 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class TokenInvalidException extends RuntimeException {

    /** @param message human-readable message describing why the token is invalid */
    public TokenInvalidException(String message) {
        super(message);
    }
}
