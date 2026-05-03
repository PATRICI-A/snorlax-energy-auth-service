package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.CuentaBloqueadaException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.EmailNotVerifiedException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidCredentialsException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User verifiedUser;
    private LoginRequestDto loginDto;

    @BeforeEach
    void setUp() {
        verifiedUser = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John", "Doe", "CS", 4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.MALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, true, 0, null, null
        );
        loginDto = LoginRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .password("password123")
                .build();
    }

    @Test
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(verifiedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        RefreshToken savedSession = new RefreshToken("id", verifiedUser.getId().toString(),
                "access-token", "refresh-token", LocalDateTime.now().plusMinutes(15),
                false, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(refreshTokenRepository.save(any())).thenReturn(savedSession);

        LoginResponseDto response = loginUseCase.login(loginDto);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(refreshTokenRepository).deleteByUserId(anyString());
    }

    @Test
    void shouldThrowInvalidCredentialsWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.login(loginDto));
    }

    @Test
    void shouldThrowCuentaBloqueadaWhenAccountIsBlocked() {
        verifiedUser.lockAccount(LocalDateTime.now().plusMinutes(30));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(verifiedUser));
        assertThrows(CuentaBloqueadaException.class, () -> loginUseCase.login(loginDto));
    }

    @Test
    void shouldNotBlockWhenBlockedUntilIsInThePast() {
        verifiedUser.lockAccount(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(verifiedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        RefreshToken savedSession = new RefreshToken("id", verifiedUser.getId().toString(),
                "access-token", "refresh-token", LocalDateTime.now().plusMinutes(15),
                false, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(refreshTokenRepository.save(any())).thenReturn(savedSession);

        assertDoesNotThrow(() -> loginUseCase.login(loginDto));
    }

    @Test
    void shouldThrowInvalidCredentialsWhenPasswordIsWrong() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(verifiedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.login(loginDto));
        verify(userRepository).save(verifiedUser);
        assertEquals(1, verifiedUser.getFailedAttempts());
    }

    @Test
    void shouldLockAccountAfterFiveFailedAttempts() {
        verifiedUser.setFailedAttempts(4);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(verifiedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.login(loginDto));
        assertEquals(5, verifiedUser.getFailedAttempts());
        assertNotNull(verifiedUser.getBlockedUntil());
    }

    @Test
    void shouldThrowEmailNotVerifiedWhenNotVerified() {
        User unverifiedUser = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John", "Doe", "CS", 4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.MALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, false, 0, null, null
        );
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(unverifiedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        assertThrows(EmailNotVerifiedException.class, () -> loginUseCase.login(loginDto));
    }
}
