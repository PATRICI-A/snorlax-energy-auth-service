package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.OtpResendEventDto;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.OtpVerificationEventDto;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.PasswordResetEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Infrastructure service that publishes auth notification events to RabbitMQ.
 * Exchange and routing keys are resolved from application properties so they
 * stay consistent with the values declared in {@code application-dev.yml}.
 */
@Service
@RequiredArgsConstructor
public class AuthNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.auth}")
    private String authExchange;

    @Value("${rabbitmq.routing-key.otp-verification}")
    private String otpVerificationKey;

    @Value("${rabbitmq.routing-key.otp-resend}")
    private String otpResendKey;

    @Value("${rabbitmq.routing-key.password-reset}")
    private String passwordResetKey;

    /**
     * Publishes an OTP verification event for a new registration.
     *
     * @param email   recipient email
     * @param otpCode 6-digit OTP
     * @param userId  user UUID; may be {@code null} for pre-registration flows
     */
    public void publishOtpVerification(String email, String otpCode, UUID userId) {
        rabbitTemplate.convertAndSend(authExchange, otpVerificationKey,
                new OtpVerificationEventDto(email, otpCode, userId));
    }

    /**
     * Publishes an OTP resend event for an existing account.
     *
     * @param email   recipient email
     * @param otpCode new 6-digit OTP
     * @param userId  user UUID of the account owner
     */
    public void publishOtpResend(String email, String otpCode, UUID userId) {
        rabbitTemplate.convertAndSend(authExchange, otpResendKey,
                new OtpResendEventDto(userId, email, otpCode));
    }

    /**
     * Publishes a password-reset event.
     *
     * @param email     recipient email
     * @param resetCode 6-digit recovery code
     * @param userId    user UUID of the account owner
     */
    public void publishPasswordReset(String email, String resetCode, UUID userId) {
        rabbitTemplate.convertAndSend(authExchange, passwordResetKey,
                new PasswordResetEventDto(email, resetCode, userId));
    }
}
