package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpMaxAttemptsException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ValidateOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpCode;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case for validating the OTP sent during registration.
 * Marks the account as verified and returns JWT tokens on success.
 * Deletes the OTP after 3 failed attempts.
 */
@Service
@RequiredArgsConstructor
public class ValidateOtpUseCase implements ValidateOtpPort {

    private static final int MAX_ATTEMPTS = 3;

    private final OtpRedisRepository otpRedisRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserServicePort userServicePort;
    private final JwtService jwtService;

    /**
     * Validates the OTP and activates the user account.
     *
     * @param request email and OTP code submitted by the user
     * @return access token and refresh token on successful validation
     * @throws OtpExpiredException      if the OTP has expired or does not exist
     * @throws OtpInvalidException      if the OTP is incorrect or already used
     * @throws OtpMaxAttemptsException  if 3 failed attempts have been reached
     */
    @Override
    public LoginResponseDto validateOtp(ValidateOtpRequestDto request) {
        new OtpCode(request.getOtp());

        String email = request.getEmail().trim().toLowerCase();

        OtpCache otp = otpRedisRepository.findById(email)
                .orElseThrow(() -> new OtpExpiredException("OTP has expired. Please request a new one"));

        if (otp.isUsed()) {
            throw new OtpInvalidException("OTP has already been used");
        }

        if (!otp.getCode().equals(request.getOtp())) {
            otp.setAttempts(otp.getAttempts() + 1);
            if (otp.getAttempts() >= MAX_ATTEMPTS) {
                otpRedisRepository.delete(otp);
                throw new OtpMaxAttemptsException(
                        "Maximum OTP attempts reached. Please request a new code via /resend-otp");
            }
            otpRedisRepository.save(otp);
            throw new OtpInvalidException("Invalid OTP");
        }

        otpRedisRepository.delete(otp);

        UserDto user = userServicePort.findByEmail(email)
                .orElseThrow(() -> new OtpInvalidException("User not found"));

        userServicePort.markUserAsVerified(user.id().toString());

        String accessToken = jwtService.generateToken(user.id().toString(), email);
        refreshTokenRepository.deleteByUserId(user.id().toString());

        RefreshToken session = new RefreshToken(
                UUID.randomUUID().toString(),
                user.id().toString(),
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