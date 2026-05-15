package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.InitVerificationRequestDto;

/**
 * Input port for starting the OTP verification flow after user registration.
 */
public interface InitVerificationPort {

    /**
     * Generates and sends an OTP to the email in the given request.
     *
     * @param dto contains the institutional email and hashed password of the new user
     */
    void initVerification(InitVerificationRequestDto dto);
}
