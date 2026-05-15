package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when a registration is attempted with an email that already has an account.
 * Mapped to HTTP 409 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /** @param message human-readable message indicating the email is already registered */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
