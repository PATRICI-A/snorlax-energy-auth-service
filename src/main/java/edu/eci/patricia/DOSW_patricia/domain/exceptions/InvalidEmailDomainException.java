package edu.eci.patricia.DOSW_patricia.domain.exceptions;

public class InvalidEmailDomainException extends RuntimeException {

    public InvalidEmailDomainException(String message) {
        super(message);
    }
}
