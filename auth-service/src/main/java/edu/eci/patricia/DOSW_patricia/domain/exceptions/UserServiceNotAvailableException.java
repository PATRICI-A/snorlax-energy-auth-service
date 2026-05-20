package edu.eci.patricia.DOSW_patricia.domain.exceptions;

public class UserServiceNotAvailableException extends RuntimeException {
    public UserServiceNotAvailableException(String message) {
        super(message);
    }
}
