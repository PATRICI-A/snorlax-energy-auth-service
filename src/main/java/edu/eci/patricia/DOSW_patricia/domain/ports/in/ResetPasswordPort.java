package edu.eci.patricia.DOSW_patricia.domain.ports.in;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;

/**
 * Input port for resetting a user's password using a recovery code.
 */
public interface ResetPasswordPort {

    /**
     * Validates the recovery code and updates the user's password.
     *
     * @param dto contains email, recovery code, and new password
     */
    void resetPassword(ResetPasswordRequestDto dto);
}
