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
        log.info("Publicando OTP event para {}", to);
        rabbitTemplate.convertAndSend(
                "auth.exchange",
                "auth.otp.verification",
                new OtpVerificationEventDto(to, otpCode)
        );
    }

    @Override
    public void sendPasswordReset(String to, String code) {
        log.info("Publicando password reset event para {}", to);
        rabbitTemplate.convertAndSend(
                "auth.exchange",
                "auth.password.reset",
                new PasswordResetEventDto(to, code, null)
        );
    }
}
