package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

public final class Password {

    private static final int MIN_LENGTH = 8;

    private final String value;

    public Password(String value) {
        if (value == null || value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
