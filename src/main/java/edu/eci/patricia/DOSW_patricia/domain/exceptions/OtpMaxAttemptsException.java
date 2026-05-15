package edu.eci.patricia.DOSW_patricia.domain.exceptions;

/**
 * Thrown when the user has exceeded the maximum number of OTP validation attempts (3).
 * The OTP is deleted — a new one must be requested via /resend-otp.
 * Mapped to HTTP 429 by {@link edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler}.
 */
public class OtpMaxAttemptsException extends RuntimeException {

    /** @param message human-readable message prompting the user to request a new OTP */
    public OtpMaxAttemptsException(String message) {
        super(message);
    }
}
