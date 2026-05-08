package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
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
class ResendOtpUseCaseTest {

    @Mock private OtpRedisRepository otpRedisRepository;
    @Mock private UserServicePort userServicePort;
    @Mock private EmailSenderPort emailSender;

    @InjectMocks private ResendOtpUseCase resendOtpUseCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";

    @Test
    void resendOtp_usuarioExiste_guardaNuevoOtpYEnviaEmail() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(
                new UserDto("uid", EMAIL, "hash", false, RolEnum.STUDENT)));

        resendOtpUseCase.resendOtp(EMAIL);

        ArgumentCaptor<OtpCache> captor = ArgumentCaptor.forClass(OtpCache.class);
        verify(otpRedisRepository).save(captor.capture());

        OtpCache guardado = captor.getValue();
        assertThat(guardado.getEmail()).isEqualTo(EMAIL);
        assertThat(guardado.getCode()).hasSize(6);
        assertThat(guardado.isUsed()).isFalse();
        assertThat(guardado.getAttempts()).isZero();

        verify(emailSender).sendOtp(eq(EMAIL), eq(guardado.getCode()));
    }

    @Test
    void resendOtp_usuarioNoExiste_lanzaOtpInvalid() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resendOtpUseCase.resendOtp(EMAIL))
                .isInstanceOf(OtpInvalidException.class);
    }
}
