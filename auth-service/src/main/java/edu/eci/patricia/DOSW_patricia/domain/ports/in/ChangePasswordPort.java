package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ChangePasswordRequestDto;

public interface ChangePasswordPort {

    void changePassword(ChangePasswordRequestDto dto);
}
