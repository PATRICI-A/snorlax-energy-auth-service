package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.*;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenUseCase refreshTokenUseCase;

    private User user;
    private RefreshToken activeSession;

    @BeforeEach
    void setUp() {
        user = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John", "Doe", "CS", 4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.MALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, true, 0, null, null
        );
        activeSession = new RefreshToken(
                "session-id",
                user.getId().toString(),
                "old-access-token",
                "valid-refresh-token",
                LocalDateTime.now().plusMinutes(15),
                false,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        when(refreshTokenRepository.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(activeSession));
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("new-access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        RefreshToken newSession = new RefreshToken("new-id", user.getId().toString(),
                "new-access-token", "new-refresh-token", LocalDateTime.now().plusMinutes(15),
                false, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(refreshTokenRepository.save(any())).thenReturn(newSession);

        LoginResponseDto response = refreshTokenUseCase.refresh("valid-refresh-token");

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        verify(refreshTokenRepository).deleteByUserId(anyString());
    }

    @Test
    void shouldThrowTokenInvalidWhenTokenNotFound() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThrows(TokenInvalidException.class, () -> refreshTokenUseCase.refresh("invalid-token"));
    }

    @Test
    void shouldThrowTokenInvalidWhenTokenIsRevoked() {
        activeSession.revoke();
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(activeSession));
        assertThrows(TokenInvalidException.class, () -> refreshTokenUseCase.refresh("valid-refresh-token"));
    }

    @Test
    void shouldThrowTokenExpiredWhenTokenIsExpired() {
        RefreshToken expiredSession = new RefreshToken(
                "expired-id", user.getId().toString(), "jwt", "refresh",
                LocalDateTime.now().plusMinutes(15), false,
                LocalDateTime.now(), LocalDateTime.now().minusDays(1)
        );
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(expiredSession));
        assertThrows(TokenExpiredException.class, () -> refreshTokenUseCase.refresh("expired-token"));
    }

    @Test
    void shouldThrowTokenInvalidWhenUserNotFound() {
        when(refreshTokenRepository.findByToken(anyString())).thenReturn(Optional.of(activeSession));
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(TokenInvalidException.class, () -> refreshTokenUseCase.refresh("valid-refresh-token"));
    }
}
