package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResendOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResendOtpUseCase implements ResendOtpPort {

    private final UserRepositoryPort userRepository;
    private final EmailSenderPort emailSender;

    @Override
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new OtpInvalidException("No account found for this email"));

        if (user.isVerified()) {
            return;
        }

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        user.setOtp(new OtpEmbedded(code, LocalDateTime.now().plusMinutes(10)));
        userRepository.save(user);

        emailSender.sendOtp(email, code);
    }
}
