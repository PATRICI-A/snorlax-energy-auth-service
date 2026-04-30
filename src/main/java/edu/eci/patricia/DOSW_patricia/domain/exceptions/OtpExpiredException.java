package edu.eci.patricia.DOSW_patricia.domain.exceptions;

public class OtpExpiredException extends RuntimeException {

    public OtpExpiredException(String message) {
        super(message);
    }
}
