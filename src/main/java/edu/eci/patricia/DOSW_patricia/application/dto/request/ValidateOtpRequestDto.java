package edu.eci.patricia.DOSW_patricia.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Application-layer DTO for the validate-OTP use case.
 * Carries the email and the 6-digit OTP the user received during registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOtpRequestDto {

    /** The user's institutional email address. */
    private String email;
    /** The 6-digit OTP code to validate. */
    private String otp;
}
