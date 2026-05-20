package edu.eci.patricia.DOSW_patricia.domain.exceptions;

public class EmailNotVerifiedException extends RuntimeException {

    public EmailNotVerifiedException(String message) {
        super(message);
    }
}
