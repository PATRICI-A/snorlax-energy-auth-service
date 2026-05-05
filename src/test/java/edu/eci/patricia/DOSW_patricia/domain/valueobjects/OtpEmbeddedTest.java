package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OtpEmbeddedTest {

    @Test
    void shouldBeValidWhenNotExpiredAndNotUsed() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        assertTrue(otp.esValido());
    }

    @Test
    void shouldNotBeValidWhenExpired() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().minusMinutes(1));
        assertFalse(otp.esValido());
    }

    @Test
    void shouldNotBeValidWhenUsed() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        otp.marcaUsado();
        assertFalse(otp.esValido());
    }

    @Test
    void shouldReportExpiredWhenPastExpiration() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().minusMinutes(1));
        assertTrue(otp.haExpirado());
    }

    @Test
    void shouldNotReportExpiredWhenFutureExpiration() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        assertFalse(otp.haExpirado());
    }

    @Test
    void shouldMarkAsUsed() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        assertFalse(otp.getUsado());
        otp.marcaUsado();
        assertTrue(otp.getUsado());
    }

    @Test
    void shouldHaveInitialIntentosOfZero() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        assertEquals(0, otp.getIntentos());
    }

    @Test
    void shouldAllowSettingAllFields() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        LocalDateTime newExpiry = LocalDateTime.now().plusMinutes(5);

        otp.setCodigo("654321");
        otp.setIntentos(3);
        otp.setUsado(true);
        otp.setExpiraEn(newExpiry);

        assertEquals("654321", otp.getCodigo());
        assertEquals(3, otp.getIntentos());
        assertTrue(otp.getUsado());
        assertEquals(newExpiry, otp.getExpiraEn());
    }

    @Test
    void shouldReturnCodigoFromGetter() {
        OtpEmbedded otp = new OtpEmbedded("999888", LocalDateTime.now().plusMinutes(10));
        assertEquals("999888", otp.getCodigo());
    }

    @Test
    void shouldIncrementIntentosOnEachCall() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        otp.incrementarIntentos();
        assertEquals(1, otp.getIntentos());
        otp.incrementarIntentos();
        assertEquals(2, otp.getIntentos());
    }

    @Test
    void shouldNotReachLimitBeforeThreeAttempts() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        otp.incrementarIntentos();
        otp.incrementarIntentos();
        assertFalse(otp.haAlcanzadoLimite());
    }

    @Test
    void shouldReachLimitAfterThreeAttempts() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        otp.incrementarIntentos();
        otp.incrementarIntentos();
        otp.incrementarIntentos();
        assertTrue(otp.haAlcanzadoLimite());
    }
}
