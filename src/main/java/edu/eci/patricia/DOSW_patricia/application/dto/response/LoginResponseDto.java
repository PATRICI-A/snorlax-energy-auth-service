package edu.eci.patricia.DOSW_patricia.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after a successful login or token refresh.
 * Contains the short-lived JWT access token and the long-lived refresh token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    /** Short-lived JWT access token (15-minute TTL by default). */
    private String accessToken;
    /** Long-lived refresh token (7-day TTL) used to obtain a new access token. */
    private String refreshToken;
    /** Token scheme — always {@code "Bearer"}. */
    private String tokenType;
}
