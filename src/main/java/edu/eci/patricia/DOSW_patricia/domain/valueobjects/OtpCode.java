package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;

public final class OtpCode {

    private static final String SIX_DIGITS = "\\d{6}";

    private final String value;

    public OtpCode(String value) {
        if (value == null || !value.matches(SIX_DIGITS)) {
            throw new OtpInvalidException("OTP must be a 6-digit number");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OtpCode other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
