package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LogoutUseCase logoutUseCase;

    @Test
    void shouldLogoutSuccessfullyWhenSessionExists() {
        String userId = "user-id";
        RefreshToken session = new RefreshToken("id", userId, "jwt", "refresh",
                LocalDateTime.now().plusMinutes(15), false, LocalDateTime.now(),
                LocalDateTime.now().plusDays(7));

        when(jwtService.extractUserId(anyString())).thenReturn(userId);
        when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(session));

        logoutUseCase.logout("some.jwt.token");

        assertTrue(session.isRevoked());
        verify(refreshTokenRepository).save(session);
    }

    @Test
    void shouldDoNothingWhenNoSessionFound() {
        when(jwtService.extractUserId(anyString())).thenReturn("user-id");
        when(refreshTokenRepository.findByUserId(anyString())).thenReturn(Optional.empty());

        logoutUseCase.logout("some.jwt.token");

        verify(refreshTokenRepository, never()).save(any());
    }
}
