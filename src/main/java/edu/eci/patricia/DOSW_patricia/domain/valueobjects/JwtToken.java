package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

/**
 * Value object representing a validated, non-blank JWT token string.
 * Throws {@link IllegalArgumentException} if the value is null or blank.
 */
public record JwtToken(String value) {

    /** @throws IllegalArgumentException if the token value is null or blank */
    public JwtToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("JWT token must not be blank");
        }
    }
}
