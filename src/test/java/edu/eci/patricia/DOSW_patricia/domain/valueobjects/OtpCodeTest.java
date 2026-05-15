package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpCodeTest {

    @Test
    void shouldCreateOtpCodeWithValidSixDigits() {
        OtpCode otp = new OtpCode("123456");
        assertEquals("123456", otp.value());
    }

    @Test
    void shouldThrowExceptionForNonNumericOtp() {
        assertThrows(OtpInvalidException.class, () -> new OtpCode("abcdef"));
    }

    @Test
    void shouldThrowExceptionForOtpWithFewerThanSixDigits() {
        assertThrows(OtpInvalidException.class, () -> new OtpCode("12345"));
    }

    @Test
    void shouldThrowExceptionForOtpWithMoreThanSixDigits() {
        assertThrows(OtpInvalidException.class, () -> new OtpCode("1234567"));
    }

    @Test
    void shouldThrowExceptionForNullOtp() {
        assertThrows(OtpInvalidException.class, () -> new OtpCode(null));
    }

    @Test
    void shouldThrowExceptionForEmptyOtp() {
        assertThrows(OtpInvalidException.class, () -> new OtpCode(""));
    }

    @Test
    void shouldBeEqualToOtpWithSameValue() {
        OtpCode otp1 = new OtpCode("123456");
        OtpCode otp2 = new OtpCode("123456");
        assertEquals(otp1, otp2);
    }

    @Test
    void shouldNotBeEqualToOtpWithDifferentValue() {
        OtpCode otp1 = new OtpCode("123456");
        OtpCode otp2 = new OtpCode("654321");
        assertNotEquals(otp1, otp2);
    }

    @Test
    void shouldHaveSameHashCodeForEqualOtps() {
        OtpCode otp1 = new OtpCode("123456");
        OtpCode otp2 = new OtpCode("123456");
        assertEquals(otp1.hashCode(), otp2.hashCode());
    }

    @Test
    void shouldEqualSelf() {
        OtpCode otp = new OtpCode("123456");
        assertEquals(otp, otp);
    }

    @Test
    void shouldNotEqualNull() {
        OtpCode otp = new OtpCode("123456");
        assertNotEquals(null, otp);
    }

    @Test
    void shouldNotEqualDifferentType() {
        OtpCode otp = new OtpCode("123456");
        assertNotEquals(otp, "123456");
    }
}
