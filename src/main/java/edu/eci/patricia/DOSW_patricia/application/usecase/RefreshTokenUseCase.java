package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.RefreshTokenPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case for rotating refresh tokens.
 * Invalidates the current refresh token and issues a new access and refresh token pair.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase implements RefreshTokenPort {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtService jwtService;

    /**
     * Exchanges a valid refresh token for a new access and refresh token pair.
     *
     * @param refreshToken the current refresh token
     * @return new access token and refresh token
     * @throws TokenInvalidException  if the refresh token is invalid or revoked
     * @throws TokenExpiredException  if the refresh token has expired
     */
    @Override
    public LoginResponseDto refresh(String refreshToken) {
        RefreshToken session = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new TokenInvalidException("Invalid refresh token"));

        if (session.isRevoked()) {
            throw new TokenInvalidException("Refresh token has been revoked");
        }

        if (session.isExpired()) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        String accessToken = jwtService.generateToken(session.getUserId(), session.getEmail());

        refreshTokenRepository.deleteByUserId(session.getUserId());

        RefreshToken newSession = new RefreshToken(
                UUID.randomUUID().toString(),
                session.getUserId(),
                session.getEmail(),
                accessToken,
                UUID.randomUUID().toString(),
                jwtService.getJwtExpirationTime(),
                false,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        newSession = refreshTokenRepository.save(newSession);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(newSession.getRefreshToken())
                .tokenType("Bearer")
                .build();
    }
}
