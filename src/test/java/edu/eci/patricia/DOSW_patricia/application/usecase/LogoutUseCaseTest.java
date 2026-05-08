package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private JwtService jwtService;
    @InjectMocks private LogoutUseCase useCase;

    @Test
    void shouldLogoutSuccessfully() {
        when(jwtService.extractUserId("valid-token")).thenReturn("user-id");

        useCase.logout("valid-token");

        verify(refreshTokenRepository).deleteByUserId("user-id");
    }

    @Test
    void shouldThrowWhenTokenIsInvalid() {
        when(jwtService.extractUserId("bad-token")).thenThrow(new TokenInvalidException("Invalid"));

        assertThrows(TokenInvalidException.class, () -> useCase.logout("bad-token"));
        verify(refreshTokenRepository, never()).deleteByUserId(anyString());
    }
}
