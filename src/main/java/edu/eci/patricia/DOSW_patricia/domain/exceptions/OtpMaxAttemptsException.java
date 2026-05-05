package edu.eci.patricia.DOSW_patricia.domain.exceptions;

public class OtpMaxAttemptsException extends RuntimeException {
    public OtpMaxAttemptsException(String message) {
        super(message);
    }
}
