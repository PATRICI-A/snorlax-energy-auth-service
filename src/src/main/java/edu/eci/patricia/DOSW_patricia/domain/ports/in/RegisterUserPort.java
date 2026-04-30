package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.RegisterRequestDto;

public interface RegisterUserPort {

    void register(RegisterRequestDto request);
}
