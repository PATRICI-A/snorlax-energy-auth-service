package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.OtpVerificationEventDto;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.dto.PasswordResetEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderAdapter implements EmailSenderPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendOtp(String to, String otpCode) {
        log.info("========================================");
        log.info("[DEV] OTP para {}: {}", to, otpCode);
        log.info("========================================");
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

    @Override
    public void sendPasswordReset(String to, String code, java.util.UUID userId) {
        log.info("========================================");
        log.info("[DEV] Codigo de recuperacion para {}: {}", to, code);
        log.info("========================================");
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
