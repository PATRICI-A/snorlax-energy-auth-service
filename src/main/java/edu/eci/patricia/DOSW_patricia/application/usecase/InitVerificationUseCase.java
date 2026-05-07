package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.InitVerificationRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.InitVerificationPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class InitVerificationUseCase implements InitVerificationPort {

    private final OtpRedisRepository otpRedisRepository;
    private final EmailSenderPort emailSender;

    @Override
    public void initVerification(InitVerificationRequestDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));

        OtpCache otpCache = OtpCache.builder()
                .email(email)
                .code(code)
                .used(false)
                .attempts(0)
                .build();

        otpRedisRepository.save(otpCache);
        emailSender.sendOtp(email, code);
    }
}
