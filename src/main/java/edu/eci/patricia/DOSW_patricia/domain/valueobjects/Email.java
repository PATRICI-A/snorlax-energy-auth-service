package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidEmailDomainException;

public final class Email {

    private static final String REQUIRED_DOMAIN = "@mail.escuelaing.edu.co";

    private final String value;

    public Email(String value) {
        if (value == null || !value.toLowerCase().endsWith(REQUIRED_DOMAIN)) {
            throw new InvalidEmailDomainException(
                    "Email must belong to the @mail.escuelaing.edu.co domain");
        }
        this.value = value.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
