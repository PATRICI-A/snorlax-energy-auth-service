package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Value("${mail.dev-mode:false}")
    private boolean devMode;

    @Override
    public void sendOtp(String to, String otpCode) {
        if (devMode) {
            log.info("[DEV MODE] OTP for {}: {}", to, otpCode);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("PATRICIA - Código de verificación");
            message.setText("""
                    Hola,

                    Tu código de verificación para PATRICIA es: %s

                    Este código es válido por 10 minutos.

                    Si no solicitaste este código, ignora este mensaje.
                    """.formatted(otpCode));
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("[DEV MODE] Email sending failed ({}). OTP for {}: {}", e.getMessage(), to, otpCode);
        }
    }

    @Override
    public void sendPasswordReset(String to, String code) {
        if (devMode) {
            log.info("[DEV MODE] Password reset code for {}: {}", to, code);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("PATRICIA - Recuperación de contraseña");
            message.setText("""
                    Hola,

                    Recibimos una solicitud para restablecer tu contraseña en PATRICIA.

                    Tu código de recuperación es: %s

                    Este código es válido por 10 minutos.

                    Si no solicitaste esto, ignora este mensaje. Tu contraseña no cambiará.
                    """.formatted(code));
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("[DEV MODE] Email sending failed ({}). Reset code for {}: {}", e.getMessage(), to, code);
        }
    }
}
