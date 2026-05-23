package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the reset-password endpoint ({@code POST /api/v1/auth/reset-password}).
 * Validates the 6-digit recovery code from forgot-password and sets a new password.
 * Both {@code newPassword} and {@code confirmPassword} must match.
 */
@Schema(description = "Request body to complete the password recovery flow using the 6-digit code received by email")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
    @Schema(description = "Institutional email address of the account whose password is being reset",
            example = "juan.perez@mail.escuelaing.edu.co")
    private String email;

    @NotBlank(message = "Recovery code is required")
    @Schema(description = "6-digit recovery code sent to the email via POST /forgot-password",
            example = "749203")
    private String code;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "New password to set (minimum 8 characters). Will be hashed with BCrypt.",
            example = "NuevaClave789!")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Must match newPassword exactly",
            example = "NuevaClave789!")
    private String confirmPassword;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatch() {
        return confirmPassword != null && confirmPassword.equals(newPassword);
    }
}
