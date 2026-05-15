package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when the supplied OTP does not match the stored code, is already used,
 * or fails format validation. Mapped to HTTP 422 by
 * {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class OtpInvalidException extends RuntimeException {

    /** @param message human-readable message describing why the OTP is invalid */
    public OtpInvalidException(String message) {
        super(message);
    }
}
