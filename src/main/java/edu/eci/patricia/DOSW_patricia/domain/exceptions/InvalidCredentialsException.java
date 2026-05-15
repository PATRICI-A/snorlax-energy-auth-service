package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when the supplied email/password pair does not match any account.
 * The message is intentionally generic to avoid leaking whether the email exists.
 * Mapped to HTTP 401 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class InvalidCredentialsException extends RuntimeException {

    /** Constructs the exception with the generic message "Invalid email or password". */
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
