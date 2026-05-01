package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ValidateOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpCode;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValidateOtpUseCase implements ValidateOtpPort {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponseDto validateOtp(ValidateOtpRequestDto request) {
        new OtpCode(request.getOtp());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new OtpInvalidException("No user found for this email"));

        OtpEmbedded otp = user.getOtp();
        if (otp == null) {
            throw new OtpInvalidException("No OTP found for this user");
        }

        if (otp.haExpirado()) {
            throw new OtpExpiredException("OTP has expired. Please request a new one");
        }

        if (Boolean.TRUE.equals(otp.getUsado()) || !otp.getCodigo().equals(request.getOtp())) {
            throw new OtpInvalidException("Invalid OTP");
        }

        otp.marcaUsado();
        user.verify();
        user.resetLockout();
        userRepository.save(user);

        String userId = user.getId().toString();
        String accessToken = jwtService.generateToken(userId, user.getEmail().getValue());

        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken session = new RefreshToken(
                UUID.randomUUID().toString(),
                userId,
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
