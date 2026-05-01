package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ForgotPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCase implements ForgotPasswordPort {

    private final UserRepositoryPort userRepository;
    private final EmailSenderPort emailSender;

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new OtpInvalidException("No account found with that email"));

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        OtpEmbedded resetOtp = new OtpEmbedded(code, LocalDateTime.now().plusMinutes(10));
        user.setPasswordResetOtp(resetOtp);
        userRepository.save(user);

        emailSender.sendPasswordReset(email.trim().toLowerCase(), code);
    }
}
