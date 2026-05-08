package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.InitVerificationRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InitVerificationUseCaseTest {

    @Mock private OtpRedisRepository otpRedisRepository;
    @Mock private EmailSenderPort emailSender;

    @InjectMocks private InitVerificationUseCase initVerificationUseCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";

    @Test
    void initVerification_guardaOtpEnRedisYEnviaEmail() {
        InitVerificationRequestDto dto = new InitVerificationRequestDto(EMAIL, "$2a$10$hash");

        initVerificationUseCase.initVerification(dto);

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
    void initVerification_normalizaEmailAMinusculas() {
        InitVerificationRequestDto dto = new InitVerificationRequestDto("USER@MAIL.ESCUELAING.EDU.CO", "$2a$10$hash");

        initVerificationUseCase.initVerification(dto);

        ArgumentCaptor<OtpCache> captor = ArgumentCaptor.forClass(OtpCache.class);
        verify(otpRedisRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("user@mail.escuelaing.edu.co");
    }
}
