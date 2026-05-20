package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter that implements {@link EmailSenderPort} by delegating to {@link AuthNotificationPublisher}.
 * If RabbitMQ is unavailable the OTP or recovery code is logged at WARN level so developers
 * can still test locally without a running broker.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderAdapter implements EmailSenderPort {

    private static final String SEPARATOR = "========================================";

    private final AuthNotificationPublisher publisher;

    /**
     * Publishes an OTP verification event to the {@code auth.otp.verification} routing key.
     * userId is {@code null} for pre-registration flows where the profile does not yet exist.
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
            publisher.publishOtpVerification(to, otpCode, null);
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — OTP no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }

    /**
     * Publishes an OTP resend event to the {@code auth.otp.resend} routing key.
     *
     * @param to      the recipient's institutional email
     * @param otpCode the new 6-digit OTP to include in the email
     * @param userId  the verified UUID of the account owner
     */
    @Override
    public void sendOtpResend(String to, String otpCode, UUID userId) {
        log.info(SEPARATOR);
        log.info("[DEV] OTP reenvio para {}: {}", to, otpCode);
        log.info(SEPARATOR);
        try {
            publisher.publishOtpResend(to, otpCode, userId);
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — OTP reenvio no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }

    /**
     * Publishes a password-reset event to the {@code auth.password.reset} routing key.
     *
     * @param to     the recipient's institutional email
     * @param code   the 6-digit recovery code to include in the email
     * @param userId the user's UUID included in the event payload
     */
    @Override
    public void sendPasswordReset(String to, String code, UUID userId) {
        log.info(SEPARATOR);
        log.info("[DEV] Codigo de recuperacion para {}: {}", to, code);
        log.info(SEPARATOR);
        try {
            publisher.publishPasswordReset(to, code, userId);
        } catch (Exception e) {
            log.warn("[DEV] RabbitMQ no disponible — codigo no enviado por email (usa el codigo del log): {}", e.getMessage());
        }
    }
}
