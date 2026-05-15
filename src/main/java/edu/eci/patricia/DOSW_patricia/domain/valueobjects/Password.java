package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

/**
 * Value object representing a validated raw password.
 * Enforces a minimum length of 8 characters. The value is never hashed here —
 * BCrypt hashing is applied by the use case before the password is stored.
 */
public record Password(String value) {

    private static final int MIN_LENGTH = 8;

    /** @throws IllegalArgumentException if the value is null or shorter than 8 characters */
    public Password {
        if (value == null || value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }
    }
}
