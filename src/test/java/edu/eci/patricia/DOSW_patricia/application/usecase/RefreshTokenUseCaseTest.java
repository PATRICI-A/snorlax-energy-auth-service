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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseTest {

    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private JwtService jwtService;

    @InjectMocks private RefreshTokenUseCase refreshTokenUseCase;

    private static final String USER_ID = "user-uuid-001";
    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String REFRESH_TOKEN = "refresh-token-abc";

    private RefreshToken sessionActiva;

    @BeforeEach
    void setUp() {
        sessionActiva = new RefreshToken(
                "id-1", USER_ID, EMAIL, "old-jwt", REFRESH_TOKEN,
                LocalDateTime.now().plusMinutes(15), false,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));
    }

    @Test
    void refresh_tokenValido_retornaNuevosTokens() {
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(sessionActiva));
        when(jwtService.generateToken(USER_ID, EMAIL)).thenReturn("nuevo-access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoginResponseDto response = refreshTokenUseCase.refresh(REFRESH_TOKEN);

        assertThat(response.getAccessToken()).isEqualTo("nuevo-access-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(refreshTokenRepository).deleteByUserId(USER_ID);
    }

    @Test
    void refresh_tokenNoExiste_lanzaTokenInvalid() {
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenUseCase.refresh(REFRESH_TOKEN))
                .isInstanceOf(TokenInvalidException.class);
    }

    @Test
    void refresh_tokenRevocado_lanzaTokenInvalid() {
        RefreshToken revocado = new RefreshToken(
                "id-1", USER_ID, EMAIL, "jwt", REFRESH_TOKEN,
                LocalDateTime.now().plusMinutes(15), true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(revocado));

        assertThatThrownBy(() -> refreshTokenUseCase.refresh(REFRESH_TOKEN))
                .isInstanceOf(TokenInvalidException.class);
    }

    @Test
    void refresh_tokenExpirado_lanzaTokenExpired() {
        RefreshToken expirado = new RefreshToken(
                "id-1", USER_ID, EMAIL, "jwt", REFRESH_TOKEN,
                LocalDateTime.now().minusMinutes(1), false,
                LocalDateTime.now().minusDays(8), LocalDateTime.now().minusSeconds(1));
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(expirado));

        assertThatThrownBy(() -> refreshTokenUseCase.refresh(REFRESH_TOKEN))
                .isInstanceOf(TokenExpiredException.class);
    }
}
