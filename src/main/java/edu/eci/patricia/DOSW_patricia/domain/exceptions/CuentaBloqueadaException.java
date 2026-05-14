package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when a login attempt is rejected because the account is temporarily locked
 * after exceeding the maximum number of consecutive failed login attempts.
 * Mapped to HTTP 422 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class CuentaBloqueadaException extends RuntimeException {

    /** @param message human-readable message indicating when the lock expires */
    public CuentaBloqueadaException(String message) {
        super(message);
    }
}
