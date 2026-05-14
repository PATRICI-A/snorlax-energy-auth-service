package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidEmailDomainException;

public record Email(String value) {

    private static final String REQUIRED_DOMAIN = "@mail.escuelaing.edu.co";

    public Email {
        if (value == null || !value.toLowerCase().endsWith(REQUIRED_DOMAIN)) {
            throw new InvalidEmailDomainException(
                    "Email must belong to the @mail.escuelaing.edu.co domain");
        }
        value = value.toLowerCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
