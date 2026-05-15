package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when an email address does not belong to the required institutional domain
 * ({@code @mail.escuelaing.edu.co}). Mapped to HTTP 400 by
 * {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class InvalidEmailDomainException extends RuntimeException {

    /** @param message human-readable message describing the domain requirement */
    public InvalidEmailDomainException(String message) {
        super(message);
    }
}
