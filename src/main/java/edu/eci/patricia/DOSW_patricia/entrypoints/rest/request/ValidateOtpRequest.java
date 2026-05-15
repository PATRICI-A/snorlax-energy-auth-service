package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the OTP-validation endpoint ({@code POST /api/v1/auth/verify-otp}).
 * Carries the institutional email and the 6-digit OTP delivered during registration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOtpRequest {

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{6}", message = "OTP must be a 6-digit number")
    private String otp;
}
