package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.OtpVerificationEventDto;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.PasswordResetEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

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

    private final RabbitTemplate rabbitTemplate;

    /**
     * Publishes an OTP verification event to the {@code auth.otp.verification} routing key.
     * Always logs the code for local development convenience.
     *
     * @param to      the recipient's institutional email
     * @param otpCode the 6-digit OTP to include in the email
     */
    @Override
    public void sendOtp(String to, String otpCode) {
        log.info(SEPARATOR);
        log.info("[DEV] OTP para {}: {}", to, otpCode);
        log.info(SEPARATOR);
        try {
            rabbitTemplate.convertAndSend(
                    "auth.exchange",
                    "auth.otp.verification",
                    new OtpVerificationEventDto(to, otpCode)
            );
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — OTP no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }

    /**
     * Publishes a password-reset event to the {@code auth.password.reset} routing key.
     * Always logs the code for local development convenience.
     *
     * @param to     the recipient's institutional email
     * @param code   the 6-digit recovery code to include in the email
     * @param userId the user's UUID included in the event payload
     */
    @Override
    public void sendPasswordReset(String to, String code, java.util.UUID userId) {
        log.info(SEPARATOR);
        log.info("[DEV] Codigo de recuperacion para {}: {}", to, code);
        log.info(SEPARATOR);
        try {
            rabbitTemplate.convertAndSend(
                    "auth.exchange",
                    "auth.password.reset",
                    new PasswordResetEventDto(to, code, userId.toString())
            );
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — codigo no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }
}
