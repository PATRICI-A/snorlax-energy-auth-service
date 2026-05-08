package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;

public interface LoginPort {

    LoginResponseDto login(LoginRequestDto dto);
}
