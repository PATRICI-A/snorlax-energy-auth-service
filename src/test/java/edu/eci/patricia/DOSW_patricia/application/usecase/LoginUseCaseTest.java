package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.CuentaBloqueadaException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.EmailNotVerifiedException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidCredentialsException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private LockoutRedisRepository lockoutRedisRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private LoginUseCase loginUseCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String PASSWORD = "Password123!";
    private static final String HASHED = "$2a$10$hashedPasswordValue";
    private static final String USER_ID = "user-uuid-001";

    private UserDto verifiedUser;
    private LoginRequestDto dto;

    @BeforeEach
    void setUp() {
        verifiedUser = new UserDto(USER_ID, EMAIL, HASHED, true, RolEnum.STUDENT);
        dto = LoginRequestDto.builder().email(EMAIL).password(PASSWORD).build();
    }

    @Test
    void login_credencialesCorrectas_retornaTokens() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(true);
        when(jwtService.generateToken(USER_ID, EMAIL)).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoginResponseDto response = loginUseCase.login(dto);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getRefreshToken()).isNotNull();
        verify(refreshTokenRepository).deleteByUserId(USER_ID);
        verify(lockoutRedisRepository).deleteById(EMAIL);
    }

    @Test
    void login_usuarioNoExiste_lanzaInvalidCredentials() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginUseCase.login(dto))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_cuentaBloqueada_lanzaCuentaBloqueada() {
        LockoutCache lockout = LockoutCache.builder().email(EMAIL).failedAttempts(5).build();
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.of(lockout));

        assertThatThrownBy(() -> loginUseCase.login(dto))
                .isInstanceOf(CuentaBloqueadaException.class);
    }

    @Test
    void login_contrasenaIncorrecta_incrementaContadorBloqueo() {
        LockoutCache existente = LockoutCache.builder().email(EMAIL).failedAttempts(2).build();
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.of(existente));
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.login(dto))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(lockoutRedisRepository).save(argThat(l -> l.getFailedAttempts() == 3));
    }

    @Test
    void login_primerIntentoFallido_creaRegistroBloqueoConUno() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(verifiedUser));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(false);

        assertThatThrownBy(() -> loginUseCase.login(dto))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(lockoutRedisRepository).save(argThat(l -> l.getFailedAttempts() == 1));
    }

    @Test
    void login_emailNoVerificado_lanzaEmailNotVerified() {
        UserDto noVerificado = new UserDto(USER_ID, EMAIL, HASHED, false, RolEnum.STUDENT);
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(noVerificado));
        when(lockoutRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(true);

        assertThatThrownBy(() -> loginUseCase.login(dto))
                .isInstanceOf(EmailNotVerifiedException.class);
    }
}
