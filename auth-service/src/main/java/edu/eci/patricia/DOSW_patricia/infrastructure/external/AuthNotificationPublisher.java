package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.application.dto.event.OtpResendEventDto;
import edu.eci.patricia.DOSW_patricia.application.dto.event.OtpVerificationEventDto;
import edu.eci.patricia.DOSW_patricia.application.dto.event.PasswordResetEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public void publishOtpVerification(UUID userId, String email, String otpCode) {
        rabbitTemplate.convertAndSend(authExchange, otpVerificationKey,
                OtpVerificationEventDto.builder()
                        .userId(userId)
                        .email(email)
                        .otpCode(otpCode)
                        .build());
    }

    public void publishOtpResend(UUID userId, String email, String otpCode) {
        rabbitTemplate.convertAndSend(authExchange, otpResendKey,
                OtpResendEventDto.builder()
                        .userId(userId)
                        .email(email)
                        .otpCode(otpCode)
                        .build());
    }

    public void publishPasswordReset(UUID userId, String email, String resetCode) {
        rabbitTemplate.convertAndSend(authExchange, passwordResetKey,
                PasswordResetEventDto.builder()
                        .userId(userId)
                        .email(email)
                        .resetCode(resetCode)
                        .build());
    }
}
