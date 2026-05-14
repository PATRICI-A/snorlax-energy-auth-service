package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;

public record OtpCode(String value) {

    private static final String SIX_DIGITS = "\\d{6}";

    public OtpCode {
        if (value == null || !value.matches(SIX_DIGITS)) {
            throw new OtpInvalidException("OTP must be a 6-digit number");
        }
    }
}
