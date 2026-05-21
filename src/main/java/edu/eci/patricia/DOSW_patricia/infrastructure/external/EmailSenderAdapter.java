package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.OtpVerificationEventDto;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.PasswordResetEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter that implements {@link EmailSenderPort} by publishing events to RabbitMQ.
 * If RabbitMQ is unavailable the OTP or recovery code is logged at WARN level so developers
 * can still test locally without a running broker.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderAdapter implements EmailSenderPort {

    private static final String SEPARATOR = "========================================";
    private static final String AUTH_EXCHANGE = "auth.exchange";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendOtp(String to, String otpCode, UUID userId) {
        log.info(SEPARATOR);
        log.info("[DEV] OTP para {}: {}", to, otpCode);
        log.info(SEPARATOR);
        try {
            rabbitTemplate.convertAndSend(AUTH_EXCHANGE, "auth.otp.verification",
                    new OtpVerificationEventDto(to, otpCode, userId));
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — OTP no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }

    @Override
    public void sendOtpResend(String to, String otpCode, UUID userId) {
        log.info(SEPARATOR);
        log.info("[DEV] OTP reenvio para {}: {}", to, otpCode);
        log.info(SEPARATOR);
        try {
            rabbitTemplate.convertAndSend(AUTH_EXCHANGE, "auth.otp.resend",
                    new OtpVerificationEventDto(to, otpCode, userId));
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — OTP reenvio no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }

    @Override
    public void sendPasswordReset(String to, String code, UUID userId) {
        log.info(SEPARATOR);
        log.info("[DEV] Codigo de recuperacion para {}: {}", to, code);
        log.info(SEPARATOR);
        try {
            rabbitTemplate.convertAndSend(AUTH_EXCHANGE, "auth.password.reset",
                    new PasswordResetEventDto(to, code, userId));
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — codigo no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }
}
