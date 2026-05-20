package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResendOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Use case for resending a new OTP to a user whose previous code expired or reached max attempts.
 */
@Service
@RequiredArgsConstructor
public class ResendOtpUseCase implements ResendOtpPort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OtpRedisRepository otpRedisRepository;
    private final UserServicePort userServicePort;
    private final EmailSenderPort emailSender;

    /**
     * Generates and sends a new OTP to the given email address.
     *
     * @param email the institutional email of the user
     * @throws OtpInvalidException if no account is found for the given email
     */
    @Override
    public void resendOtp(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        var user = userServicePort.findByEmail(normalizedEmail)
                .orElseThrow(() -> new OtpInvalidException("No account found for this email"));

        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        OtpCache otpCache = OtpCache.builder()
                .email(normalizedEmail)
                .code(code)
                .used(false)
                .attempts(0)
                .build();

        otpRedisRepository.save(otpCache);
        emailSender.sendOtpResend(normalizedEmail, code, user.id());
    }
}
