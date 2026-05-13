package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.CuentaBloqueadaException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.EmailNotVerifiedException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidCredentialsException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LoginPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.LockoutCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.LockoutRedisRepository;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginUseCase implements LoginPort {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserServicePort userServicePort;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final LockoutRedisRepository lockoutRedisRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {
        String email = dto.getEmail().trim().toLowerCase();

        UserDto user = userServicePort.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        lockoutRedisRepository.findById(email).ifPresent(lockout -> {
            if (lockout.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                throw new CuentaBloqueadaException(
                        "Account temporarily locked. Please try again in 30 minutes.");
            }
        });

        if (!passwordEncoder.matches(dto.getPassword(), user.passwordHash())) {
            LockoutCache lockout = lockoutRedisRepository.findById(email)
                    .orElse(LockoutCache.builder().email(email).failedAttempts(0).build());
            lockout.setFailedAttempts(lockout.getFailedAttempts() + 1);
            lockoutRedisRepository.save(lockout);
            throw new InvalidCredentialsException();
        }

        if (!user.verified()) {
            throw new EmailNotVerifiedException("Email not verified. Check your inbox for the OTP.");
        }

        lockoutRedisRepository.deleteById(email);

        String userId = user.id().toString();
        String accessToken = jwtService.generateToken(userId, email);

        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken session = new RefreshToken(
                UUID.randomUUID().toString(),
                userId,
                email,
                accessToken,
                UUID.randomUUID().toString(),
                jwtService.getJwtExpirationTime(),
                false,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        session = refreshTokenRepository.save(session);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(session.getRefreshToken())
                .tokenType("Bearer")
                .build();
    }
}
