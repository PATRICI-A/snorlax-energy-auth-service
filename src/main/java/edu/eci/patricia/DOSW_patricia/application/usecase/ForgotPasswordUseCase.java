package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ForgotPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.PasswordResetOtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.PasswordResetOtpRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCase implements ForgotPasswordPort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserServicePort userServicePort;
    private final PasswordResetOtpRedisRepository passwordResetOtpRedisRepository;
    private final EmailSenderPort emailSender;

    @Override
    public void forgotPassword(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        var user = userServicePort.findByEmail(normalizedEmail)
                .orElseThrow(() -> new OtpInvalidException("No account found with that email"));

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        PasswordResetOtpCache resetOtp = PasswordResetOtpCache.builder()
                .email(normalizedEmail)
                .code(code)
                .used(false)
                .build();

        passwordResetOtpRedisRepository.save(resetOtp);
        emailSender.sendPasswordReset(normalizedEmail, code, user.id());
    }
}
