package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.InitVerificationRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.InitVerificationPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Use case for initializing the OTP verification flow after user registration.
 * Generates a 6-digit OTP, stores it in cache and sends it to the user's email.
 */
@Service
@RequiredArgsConstructor
public class InitVerificationUseCase implements InitVerificationPort {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OtpRedisRepository otpRedisRepository;
    private final EmailSenderPort emailSender;

    /**
     * Generates and sends an OTP to the email provided in the registration request.
     *
     * @param dto contains the institutional email and hashed password of the new user
     */
    @Override
    public void initVerification(InitVerificationRequestDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        String code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        OtpCache otpCache = OtpCache.builder()
                .email(email)
                .code(code)
                .used(false)
                .attempts(0)
                .build();

        otpRedisRepository.save(otpCache);
        emailSender.sendOtp(email, code, null);
    }
}
