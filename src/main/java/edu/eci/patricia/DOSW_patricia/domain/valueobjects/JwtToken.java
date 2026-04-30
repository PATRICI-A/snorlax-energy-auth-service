package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

public record JwtToken(String value) {

    public JwtToken {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("JWT token must not be blank");
        }
    }
}
