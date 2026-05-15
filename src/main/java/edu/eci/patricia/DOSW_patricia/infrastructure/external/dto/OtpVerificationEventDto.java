package edu.eci.patricia.DOSW_patricia.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event payload published to RabbitMQ when a registration OTP is generated.
 * The email notification service consumes this from the {@code auth.otp.verification} routing key.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationEventDto {
    /** Recipient's institutional email address. */
    private String email;
    /** The 6-digit OTP to be delivered to the user. */
    private String otpCode;
}
