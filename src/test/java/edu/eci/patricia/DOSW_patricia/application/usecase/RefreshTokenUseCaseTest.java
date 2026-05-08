package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import org.junit.jupiter.api.BeforeEach;
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
class RefreshTokenUseCaseTest {

    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private JwtService jwtService;
    @InjectMocks private RefreshTokenUseCase useCase;

    private static final String TOKEN = "refresh-token-uuid";
    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String USER_ID = "user-id";

    private RefreshToken validSession;
    private RefreshToken savedSession;

    @BeforeEach
    void setUp() {
        validSession = new RefreshToken("id", USER_ID, EMAIL, "old-jwt", TOKEN,
                LocalDateTime.now().plusMinutes(15), false,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        savedSession = new RefreshToken("new-id", USER_ID, EMAIL, "new-jwt", "new-refresh",
                LocalDateTime.now().plusMinutes(15), false,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));
    }

    @Test
    void shouldRefreshAndRotateTokensSuccessfully() {
        when(refreshTokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(validSession));
        when(jwtService.generateToken(USER_ID, EMAIL)).thenReturn("new-jwt");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(refreshTokenRepository.save(any())).thenReturn(savedSession);

        LoginResponseDto result = useCase.refresh(TOKEN);

        assertNotNull(result);
        assertEquals("Bearer", result.getTokenType());
        verify(refreshTokenRepository).deleteByUserId(USER_ID);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowWhenTokenNotFound() {
        when(refreshTokenRepository.findByToken(TOKEN)).thenReturn(Optional.empty());

        assertThrows(TokenInvalidException.class, () -> useCase.refresh(TOKEN));
    }

    @Test
    void shouldThrowWhenTokenIsRevoked() {
        RefreshToken revoked = new RefreshToken("id", USER_ID, EMAIL, "jwt", TOKEN,
                LocalDateTime.now().plusMinutes(15), true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(refreshTokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(revoked));

        assertThrows(TokenInvalidException.class, () -> useCase.refresh(TOKEN));
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        RefreshToken expired = new RefreshToken("id", USER_ID, EMAIL, "jwt", TOKEN,
                LocalDateTime.now().plusMinutes(15), false,
                LocalDateTime.now().minusDays(8), LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken(TOKEN)).thenReturn(Optional.of(expired));

        assertThrows(TokenExpiredException.class, () -> useCase.refresh(TOKEN));
    }
}
