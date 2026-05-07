package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;

public interface ResetPasswordPort {

    void resetPassword(ResetPasswordRequestDto dto);
}
