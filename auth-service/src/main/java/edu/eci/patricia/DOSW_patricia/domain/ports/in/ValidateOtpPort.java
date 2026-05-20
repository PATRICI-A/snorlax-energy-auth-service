package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;

public interface ValidateOtpPort {

    LoginResponseDto validateOtp(ValidateOtpRequestDto request);
}
