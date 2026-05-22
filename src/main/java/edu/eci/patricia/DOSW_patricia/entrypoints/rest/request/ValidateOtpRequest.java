package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the OTP-validation endpoint ({@code POST /api/v1/auth/verify-otp}).
 * Carries the institutional email and the 6-digit OTP delivered during registration.
 */
@Schema(description = "Request body to verify the OTP sent during registration and activate the account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOtpRequest {

    @NotBlank(message = "Email is required")
    @Schema(description = "Institutional email address associated with the pending account",
            example = "juan.perez@mail.escuelaing.edu.co")
    private String email;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be a 6-digit number")
    @Schema(description = "6-digit numeric OTP received by email during registration",
            example = "482917")
    private String otp;
}
