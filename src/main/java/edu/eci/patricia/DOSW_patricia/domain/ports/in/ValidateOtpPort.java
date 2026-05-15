package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;

/**
 * Input port for validating the OTP sent during registration.
 */
public interface ValidateOtpPort {

    /**
     * Validates the OTP and activates the user account.
     *
     * @param request email and OTP code submitted by the user
     * @return access token and refresh token on successful validation
     */
    LoginResponseDto validateOtp(ValidateOtpRequestDto request);
}
