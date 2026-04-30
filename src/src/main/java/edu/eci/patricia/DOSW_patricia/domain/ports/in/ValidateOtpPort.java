package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;

public interface ValidateOtpPort {

    void validateOtp(ValidateOtpRequestDto request);
}
