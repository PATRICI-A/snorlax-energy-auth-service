package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

public record Password(String value) {

    private static final int MIN_LENGTH = 8;

    public Password {
        if (value == null || value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }
    }
}
