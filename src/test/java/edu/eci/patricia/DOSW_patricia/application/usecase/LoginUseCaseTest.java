package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.CuentaBloqueadaException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.EmailNotVerifiedException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidCredentialsException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.LockoutCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.LockoutRedisRepository;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private LockoutRedisRepository lockoutRedisRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private LoginUseCase useCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String PASSWORD = "Test1234!";
    private static final String HASHED = "$2a$10$hash";

    private UserDto verifiedUser;
    private UserDto unverifiedUser;
    private RefreshToken savedToken;

    @BeforeEach
    void setUp() {
        verifiedUser = new UserDto("user-id", EMAIL, HASHED, true, RolEnum.STUDENT);
        unverifiedUser = new UserDto("user-id", EMAIL, HASHED, false, RolEnum.STUDENT);
        savedToken = new RefreshToken("id", "user-id", EMAIL, "access-token", "refresh-uuid",
                LocalDateTime.now().plusMinutes(15), false, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
    }

    @Test
    void shouldLoginSuccessfully() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(refreshTokenRepository.save(any())).thenReturn(savedToken);

        LoginResponseDto result = useCase.login(new LoginRequestDto(EMAIL, PASSWORD));

        assertNotNull(result);
        assertEquals("Bearer", result.getTokenType());
        assertNotNull(result.getAccessToken());
        verify(lockoutRedisRepository).deleteById(EMAIL);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.login(new LoginRequestDto(EMAIL, PASSWORD)));
    }

    @Test
    void shouldThrowWhenAccountIsLocked() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        LockoutCache lockout = LockoutCache.builder().email(EMAIL).failedAttempts(5).build();
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.of(lockout));

        assertThrows(CuentaBloqueadaException.class,
                () -> useCase.login(new LoginRequestDto(EMAIL, PASSWORD)));
    }

    @Test
    void shouldThrowAndIncrementLockoutOnWrongPassword() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.login(new LoginRequestDto(EMAIL, PASSWORD)));
        verify(lockoutRedisRepository).save(any(LockoutCache.class));
    }

    @Test
    void shouldIncrementExistingLockoutCounterOnWrongPassword() {
        LockoutCache existing = LockoutCache.builder().email(EMAIL).failedAttempts(2).build();
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.login(new LoginRequestDto(EMAIL, PASSWORD)));
        verify(lockoutRedisRepository).save(argThat(l -> l.getFailedAttempts() == 3));
    }

    @Test
    void shouldThrowWhenEmailNotVerified() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(unverifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(true);

        assertThrows(EmailNotVerifiedException.class,
                () -> useCase.login(new LoginRequestDto(EMAIL, PASSWORD)));
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(true);
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(refreshTokenRepository.save(any())).thenReturn(savedToken);

        useCase.login(new LoginRequestDto("USER@mail.escuelaing.edu.co", PASSWORD));

        verify(userServicePort).findByEmail(EMAIL);
    }
}
