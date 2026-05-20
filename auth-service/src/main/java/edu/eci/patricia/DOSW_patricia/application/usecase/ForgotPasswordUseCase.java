package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ForgotPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.PasswordResetOtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.PasswordResetOtpRedisRepository;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.AuthNotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCase implements ForgotPasswordPort {

    private final UserServicePort userServicePort;
    private final PasswordResetOtpRedisRepository passwordResetOtpRedisRepository;
    private final AuthNotificationPublisher notificationPublisher;

    @Override
    public void forgotPassword(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        UserDto user = userServicePort.findByEmail(normalizedEmail)
                .orElseThrow(() -> new OtpInvalidException("No account found with that email"));

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        PasswordResetOtpCache resetOtp = PasswordResetOtpCache.builder()
                .email(normalizedEmail)
                .code(code)
                .used(false)
                .build();

        passwordResetOtpRedisRepository.save(resetOtp);
        notificationPublisher.publishPasswordReset(UUID.fromString(user.id()), normalizedEmail, code);
    }
}
