package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "SnorlaxEnergy2026SecretKeyParaJWT!!");
        ReflectionTestUtils.setField(jwtService, "expiration", 900000L);
    }

    @Test
    void shouldGenerateNonEmptyToken() {
        String token = jwtService.generateToken("user-id", "user@mail.escuelaing.edu.co");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldGenerateTokenWithThreeParts() {
        String token = jwtService.generateToken("user-id", "user@mail.escuelaing.edu.co");
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void shouldValidateGeneratedTokenAndReturnSubject() {
        String token = jwtService.generateToken("user-id", "user@mail.escuelaing.edu.co");
        Jws<Claims> result = jwtService.validateToken(token);
        assertNotNull(result);
        assertEquals("user-id", result.getPayload().getSubject());
    }

    @Test
    void shouldExtractUserIdFromToken() {
        String token = jwtService.generateToken("user-123", "user@mail.escuelaing.edu.co");
        String userId = jwtService.extractUserId(token);
        assertEquals("user-123", userId);
    }

    @Test
    void shouldThrowTokenInvalidForMalformedToken() {
        assertThrows(TokenInvalidException.class,
                () -> jwtService.validateToken("this.is.not.a.valid.jwt"));
    }

    @Test
    void shouldThrowTokenInvalidForEmptyToken() {
        assertThrows(TokenInvalidException.class,
                () -> jwtService.validateToken(""));
    }

    @Test
    void shouldReturnFutureExpirationTime() {
        LocalDateTime expiration = jwtService.getJwtExpirationTime();
        assertNotNull(expiration);
        assertTrue(expiration.isAfter(LocalDateTime.now()));
    }

    @Test
    void shouldIncludeEmailClaimInToken() {
        String token = jwtService.generateToken("user-id", "user@mail.escuelaing.edu.co");
        Jws<Claims> result = jwtService.validateToken(token);
        assertEquals("user@mail.escuelaing.edu.co", result.getPayload().get("email", String.class));
    }
}
