package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LogoutPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase implements LogoutPort {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    public void logout(String token) {
        String userId = jwtService.extractUserId(token);
        refreshTokenRepository.findByUserId(userId).ifPresent(session -> {
            session.revoke();
            refreshTokenRepository.save(session);
        });
    }
}
