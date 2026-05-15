package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidEmailDomainException;

/**
 * Value object representing a validated institutional email address.
 * Accepts only addresses ending in {@code @mail.escuelaing.edu.co} (case-insensitive).
 * The stored value is always normalised to lower-case.
 */
public record Email(String value) {

    private static final String REQUIRED_DOMAIN = "@mail.escuelaing.edu.co";

    /** @throws InvalidEmailDomainException if the address does not end in the required domain */
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
