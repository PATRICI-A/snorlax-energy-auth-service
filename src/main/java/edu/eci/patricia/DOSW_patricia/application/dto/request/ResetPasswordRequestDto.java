package edu.eci.patricia.DOSW_patricia.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Application-layer DTO for the reset-password use case.
 * Carries the email, the 6-digit recovery code from the forgot-password flow,
 * and the new raw password to set.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

    /** The user's institutional email address. */
    private String email;
    /** The 6-digit recovery code sent via forgot-password. */
    private String code;
    /** The new raw password to set (must be at least 8 characters). */
    private String newPassword;
}
