package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when an OTP validation is attempted after the code's 10-minute TTL has elapsed.
 * Mapped to HTTP 422 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class OtpExpiredException extends RuntimeException {

    /** @param message human-readable message indicating the OTP has expired */
    public OtpExpiredException(String message) {
        super(message);
    }
}
