package edu.eci.patricia.DOSW_patricia.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Application-layer DTO for the change-password use case.
 * Carries the authenticated user's ID alongside the current and new raw passwords.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

    /** ID of the authenticated user requesting the password change. */
    private String userId;
    /** The user's current raw password for verification. */
    private String currentPassword;
    /** The new raw password to set (must be at least 8 characters). */
    private String newPassword;
}
