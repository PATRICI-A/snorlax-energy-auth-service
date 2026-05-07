package edu.eci.patricia.DOSW_patricia.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private RefreshToken createToken(LocalDateTime expiraRefresh, Boolean revocado) {
        return new RefreshToken(
                "token-id",
                "user-id",
                "user@mail.escuelaing.edu.co",
                "jwt-token",
                "refresh-token",
                LocalDateTime.now().plusMinutes(15),
                revocado,
                LocalDateTime.now(),
                expiraRefresh
        );
    }

    @Test
    void shouldNotBeExpiredForFutureExpiration() {
        RefreshToken token = createToken(LocalDateTime.now().plusDays(7), false);
        assertFalse(token.isExpired());
    }

    @Test
    void shouldBeExpiredForPastExpiration() {
        RefreshToken token = createToken(LocalDateTime.now().minusDays(1), false);
        assertTrue(token.isExpired());
    }

    @Test
    void shouldBeRevokedAfterRevoke() {
        RefreshToken token = createToken(LocalDateTime.now().plusDays(7), false);
        assertFalse(token.isRevoked());
        token.revoke();
        assertTrue(token.isRevoked());
    }

    @Test
    void shouldReturnTrueForIsRevokedWhenRevocadoIsTrue() {
        RefreshToken token = createToken(LocalDateTime.now().plusDays(7), true);
        assertTrue(token.isRevoked());
    }

    @Test
    void shouldReturnFalseForIsRevokedWhenRevocadoIsNull() {
        RefreshToken token = createToken(LocalDateTime.now().plusDays(7), null);
        assertFalse(token.isRevoked());
    }

    @Test
    void shouldGetAllFields() {
        RefreshToken token = createToken(LocalDateTime.now().plusDays(7), false);
        assertEquals("token-id", token.getId());
        assertEquals("user-id", token.getUserId());
        assertEquals("jwt-token", token.getJwt());
        assertEquals("refresh-token", token.getRefreshToken());
        assertNotNull(token.getExpiraJwt());
        assertFalse(token.getRevocado());
        assertNotNull(token.getCreatedAt());
        assertNotNull(token.getExpiraRefresh());
    }

    @Test
    void shouldSetRevocado() {
        RefreshToken token = createToken(LocalDateTime.now().plusDays(7), false);
        token.setRevocado(true);
        assertTrue(token.getRevocado());
    }
}
