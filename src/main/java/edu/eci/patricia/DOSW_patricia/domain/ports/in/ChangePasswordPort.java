package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ChangePasswordRequestDto;

/**
 * Input port for changing an authenticated user's password.
 */
public interface ChangePasswordPort {

    /**
     * Changes the user's password after verifying the current one.
     *
     * @param dto contains userId, current password, and new password
     */
    void changePassword(ChangePasswordRequestDto dto);
}
