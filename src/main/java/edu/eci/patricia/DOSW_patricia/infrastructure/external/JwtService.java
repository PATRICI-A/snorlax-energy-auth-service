package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service for generating, validating and parsing JWT tokens using HMAC-SHA signing.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:900000}")
    private long expiration;

    /**
     * Builds the HMAC-SHA signing key from the configured secret.
     *
     * @return the signing key
     */
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT token for the given user.
     *
     * @param userId the user's unique identifier (subject)
     * @param email  the user's email included as a claim
     * @return signed JWT string
     */
    public String generateToken(String userId, String email) {
        return Jwts.builder()
                .subject(userId)
                .issuer("patricia-auth-service")
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey())
                .compact();
    }

    /**
     * Validates and parses a JWT token.
     *
     * @param token the JWT string to validate
     * @return parsed claims if valid
     * @throws TokenInvalidException if the token is invalid or expired
     */
    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            throw new TokenInvalidException("Invalid or expired token");
        }
    }

    /**
     * Extracts the user ID (subject) from a JWT token.
     *
     * @param token the JWT string
     * @return the user ID stored in the token subject
     */
    public String extractUserId(String token) {
        return validateToken(token).getPayload().getSubject();
    }

    /**
     * Returns the expiration timestamp for a token generated at the current moment.
     *
     * @return expiration time as LocalDateTime
     */
    public java.time.LocalDateTime getJwtExpirationTime() {
        return java.time.LocalDateTime.now().plusNanos(expiration * 1_000_000L);
    }
}
