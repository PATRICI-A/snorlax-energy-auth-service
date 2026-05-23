package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.InitVerificationRequestDto;

public interface InitVerificationPort {

    void initVerification(InitVerificationRequestDto dto);
}
