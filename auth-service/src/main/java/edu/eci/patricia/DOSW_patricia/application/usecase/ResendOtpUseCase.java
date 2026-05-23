package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResendOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.AuthNotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResendOtpUseCase implements ResendOtpPort {

    private final OtpRedisRepository otpRedisRepository;
    private final UserServicePort userServicePort;
    private final AuthNotificationPublisher notificationPublisher;

    @Override
    public void resendOtp(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        UserDto user = userServicePort.findByEmail(normalizedEmail)
                .orElseThrow(() -> new OtpInvalidException("No account found for this email"));

        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        OtpCache otpCache = OtpCache.builder()
                .email(normalizedEmail)
                .code(code)
                .used(false)
                .attempts(0)
                .build();

        otpRedisRepository.save(otpCache);
        notificationPublisher.publishOtpResend(UUID.fromString(user.id()), normalizedEmail, code);
    }
}
