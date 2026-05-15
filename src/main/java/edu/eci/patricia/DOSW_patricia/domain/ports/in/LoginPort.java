package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;

/**
 * Input port for authenticating a user with email and password.
 */
public interface LoginPort {

    /**
     * Authenticates the user and returns a JWT access token and refresh token.
     *
     * @param dto login credentials (email and password)
     * @return access token and refresh token
     */
    LoginResponseDto login(LoginRequestDto dto);
}
