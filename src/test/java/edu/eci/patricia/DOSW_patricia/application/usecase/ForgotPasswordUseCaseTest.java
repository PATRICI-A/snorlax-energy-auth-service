package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.PasswordResetOtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.PasswordResetOtpRedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private PasswordResetOtpRedisRepository passwordResetOtpRedisRepository;
    @Mock private EmailSenderPort emailSender;

    @InjectMocks private ForgotPasswordUseCase forgotPasswordUseCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";

    @Test
    void forgotPassword_usuarioExiste_guardaCodigoYEnviaEmail() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(
                new UserDto("uid", EMAIL, "hash", true, RolEnum.STUDENT)));

        forgotPasswordUseCase.forgotPassword(EMAIL);

        ArgumentCaptor<PasswordResetOtpCache> captor = ArgumentCaptor.forClass(PasswordResetOtpCache.class);
        verify(passwordResetOtpRedisRepository).save(captor.capture());

        PasswordResetOtpCache guardado = captor.getValue();
        assertThat(guardado.getEmail()).isEqualTo(EMAIL);
        assertThat(guardado.getCode()).hasSize(6);
        assertThat(guardado.isUsed()).isFalse();

        verify(emailSender).sendPasswordReset(eq(EMAIL), eq(guardado.getCode()));
    }

    @Test
    void forgotPassword_usuarioNoExiste_lanzaOtpInvalid() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> forgotPasswordUseCase.forgotPassword(EMAIL))
                .isInstanceOf(OtpInvalidException.class);
    }
}
