package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.PasswordResetOtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.PasswordResetOtpRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private PasswordResetOtpRedisRepository passwordResetOtpRedisRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private ResetPasswordUseCase resetPasswordUseCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String CODE = "654321";

    private ResetPasswordRequestDto dto;
    private PasswordResetOtpCache resetOtpValido;

    @BeforeEach
    void setUp() {
        dto = ResetPasswordRequestDto.builder()
                .email(EMAIL).code(CODE).newPassword("NuevaPass123!").build();
        resetOtpValido = PasswordResetOtpCache.builder()
                .email(EMAIL).code(CODE).used(false).build();
    }

    @Test
    void resetPassword_todoValido_actualizaContrasena() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(
                new UserDto("uid", EMAIL, "hash", true, RolEnum.STUDENT)));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(resetOtpValido));
        when(passwordEncoder.encode("NuevaPass123!")).thenReturn("$2a$10$newHash");

        resetPasswordUseCase.resetPassword(dto);

        verify(passwordResetOtpRedisRepository).save(any());
        verify(userServicePort).updatePassword(eq(EMAIL), eq("$2a$10$newHash"));
    }

    @Test
    void resetPassword_usuarioNoExiste_lanzaOtpInvalid() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordUseCase.resetPassword(dto))
                .isInstanceOf(OtpInvalidException.class);
    }

    @Test
    void resetPassword_codigoExpirado_lanzaOtpExpired() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(
                new UserDto("uid", EMAIL, "hash", true, RolEnum.STUDENT)));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordUseCase.resetPassword(dto))
                .isInstanceOf(OtpExpiredException.class);
    }

    @Test
    void resetPassword_codigoIncorrecto_lanzaOtpInvalid() {
        ResetPasswordRequestDto dtoMalo = ResetPasswordRequestDto.builder()
                .email(EMAIL).code("000000").newPassword("NuevaPass123!").build();
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(
                new UserDto("uid", EMAIL, "hash", true, RolEnum.STUDENT)));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(resetOtpValido));

        assertThatThrownBy(() -> resetPasswordUseCase.resetPassword(dtoMalo))
                .isInstanceOf(OtpInvalidException.class);
    }

    @Test
    void resetPassword_codigoYaUsado_lanzaOtpInvalid() {
        PasswordResetOtpCache usado = PasswordResetOtpCache.builder()
                .email(EMAIL).code(CODE).used(true).build();
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(
                new UserDto("uid", EMAIL, "hash", true, RolEnum.STUDENT)));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(usado));

        assertThatThrownBy(() -> resetPasswordUseCase.resetPassword(dto))
                .isInstanceOf(OtpInvalidException.class);
    }
}
