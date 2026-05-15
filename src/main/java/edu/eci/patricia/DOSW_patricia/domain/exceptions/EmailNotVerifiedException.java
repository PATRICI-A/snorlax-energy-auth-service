package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when a login attempt is made for an account whose email has not yet been
 * verified via OTP. Mapped to HTTP 403 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class EmailNotVerifiedException extends RuntimeException {

    /** @param message human-readable message prompting the user to complete OTP verification */
    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
