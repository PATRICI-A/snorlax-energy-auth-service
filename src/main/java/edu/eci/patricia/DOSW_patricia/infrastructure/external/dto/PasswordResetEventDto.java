package edu.eci.patricia.DOSW_patricia.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Event payload published to RabbitMQ when a password-reset code is generated.
 * The email notification service consumes this from the {@code auth.password.reset} routing key.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEventDto {
    /** Recipient's institutional email address. */
    private String email;
    /** The 6-digit recovery code to be delivered to the user. */
    private String resetCode;
    /** The user's UUID, included so the notification service can personalise the email. */
    private UUID userId;
}
