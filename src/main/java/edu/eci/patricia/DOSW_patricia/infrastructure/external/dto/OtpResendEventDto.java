package edu.eci.patricia.DOSW_patricia.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Event payload published to RabbitMQ when an OTP is resent to an existing user.
 * The email notification service consumes this from the {@code auth.otp.resend} routing key.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpResendEventDto {
    /** The user's UUID; always present because the account already exists at resend time. */
    private UUID userId;
    /** Recipient's institutional email address. */
    private String email;
    /** The new 6-digit OTP to be delivered to the user. */
    private String otpCode;
}
