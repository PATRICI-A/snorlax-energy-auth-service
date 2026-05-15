package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;

/**
 * Input port for rotating refresh tokens and issuing new access tokens.
 */
public interface RefreshTokenPort {

    /**
     * Exchanges a valid refresh token for a new access and refresh token pair.
     *
     * @param refreshToken the current refresh token
     * @return new access token and refresh token
     */
    LoginResponseDto refresh(String refreshToken);
}
