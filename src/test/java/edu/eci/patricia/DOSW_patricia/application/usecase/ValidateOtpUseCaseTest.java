package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpMaxAttemptsException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateOtpUseCaseTest {

    @Mock private OtpRedisRepository otpRedisRepository;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private UserServicePort userServicePort;
    @Mock private JwtService jwtService;

    @InjectMocks private ValidateOtpUseCase validateOtpUseCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String CODE = "123456";
    private static final String USER_ID = "user-uuid-001";

    private ValidateOtpRequestDto dto;
    private OtpCache otpValido;

    @BeforeEach
    void setUp() {
        dto = ValidateOtpRequestDto.builder().email(EMAIL).otp(CODE).build();
        otpValido = OtpCache.builder().email(EMAIL).code(CODE).used(false).attempts(0).build();
    }

    @Test
    void validateOtp_codigoCorrecto_retornaTokensYMarcaVerificado() {
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(otpValido));
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(
                new UserDto(USER_ID, EMAIL, "hash", true, RolEnum.STUDENT)));
        when(jwtService.generateToken(USER_ID, EMAIL)).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LoginResponseDto response = validateOtpUseCase.validateOtp(dto);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(userServicePort).markUserAsVerified(EMAIL);
        verify(otpRedisRepository).delete(otpValido);
    }

    @Test
    void validateOtp_otpNoExisteEnRedis_lanzaOtpExpired() {
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validateOtpUseCase.validateOtp(dto))
                .isInstanceOf(OtpExpiredException.class);
    }

    @Test
    void validateOtp_otpYaUsado_lanzaOtpInvalid() {
        OtpCache usado = OtpCache.builder().email(EMAIL).code(CODE).used(true).attempts(0).build();
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(usado));

        assertThatThrownBy(() -> validateOtpUseCase.validateOtp(dto))
                .isInstanceOf(OtpInvalidException.class);
    }

    @Test
    void validateOtp_codigoIncorrecto_incrementaIntentos() {
        ValidateOtpRequestDto dtoMalo = ValidateOtpRequestDto.builder().email(EMAIL).otp("999999").build();
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(otpValido));

        assertThatThrownBy(() -> validateOtpUseCase.validateOtp(dtoMalo))
                .isInstanceOf(OtpInvalidException.class);
        verify(otpRedisRepository).save(argThat(o -> o.getAttempts() == 1));
    }

    @Test
    void validateOtp_tercerintentoFallido_eliminaOtpYLanzaMaxAttempts() {
        OtpCache dosIntentos = OtpCache.builder().email(EMAIL).code(CODE).used(false).attempts(2).build();
        ValidateOtpRequestDto dtoMalo = ValidateOtpRequestDto.builder().email(EMAIL).otp("999999").build();
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(dosIntentos));

        assertThatThrownBy(() -> validateOtpUseCase.validateOtp(dtoMalo))
                .isInstanceOf(OtpMaxAttemptsException.class);
        verify(otpRedisRepository).delete(dosIntentos);
    }
}
