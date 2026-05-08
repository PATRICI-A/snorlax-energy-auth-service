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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitVerificationUseCaseTest {

    @Mock private OtpRedisRepository otpRedisRepository;
    @Mock private EmailSenderPort emailSender;
    @InjectMocks private InitVerificationUseCase useCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";

    @Test
    void shouldSaveOtpAndSendEmail() {
        useCase.initVerification(new InitVerificationRequestDto(EMAIL, "hash"));

        verify(otpRedisRepository).save(any(OtpCache.class));
        verify(emailSender).sendOtp(eq(EMAIL), anyString());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        useCase.initVerification(new InitVerificationRequestDto("User@mail.escuelaing.edu.co", "hash"));

        ArgumentCaptor<OtpCache> captor = ArgumentCaptor.forClass(OtpCache.class);
        verify(otpRedisRepository).save(captor.capture());
        assertEquals("user@mail.escuelaing.edu.co", captor.getValue().getEmail());
    }

    @Test
    void shouldGenerateSixDigitOtp() {
        useCase.initVerification(new InitVerificationRequestDto(EMAIL, "hash"));

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSender).sendOtp(anyString(), otpCaptor.capture());
        assertTrue(otpCaptor.getValue().matches("\\d{6}"));
    }

    @Test
    void shouldSaveOtpWithUnusedAndZeroAttempts() {
        useCase.initVerification(new InitVerificationRequestDto(EMAIL, "hash"));

        ArgumentCaptor<OtpCache> captor = ArgumentCaptor.forClass(OtpCache.class);
        verify(otpRedisRepository).save(captor.capture());
        assertFalse(captor.getValue().isUsed());
        assertEquals(0, captor.getValue().getAttempts());
    }
}
