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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResendOtpUseCaseTest {

    @Mock private OtpRedisRepository otpRedisRepository;
    @Mock private UserServicePort userServicePort;
    @Mock private EmailSenderPort emailSender;
    @InjectMocks private ResendOtpUseCase useCase;

    private static final UUID USER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String EMAIL = "user@mail.escuelaing.edu.co";

    @Test
    void shouldResendOtpSuccessfully() {
        UserDto user = new UserDto(USER_UUID, EMAIL, "hashed", false, RolEnum.STUDENT);
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        useCase.resendOtp(EMAIL);

        verify(otpRedisRepository).save(any(OtpCache.class));
        verify(emailSender).sendOtp(eq(EMAIL), anyString());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(OtpInvalidException.class, () -> useCase.resendOtp(EMAIL));
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        UserDto user = new UserDto(USER_UUID, EMAIL, "hashed", false, RolEnum.STUDENT);
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        useCase.resendOtp("USER@mail.escuelaing.edu.co");

        verify(userServicePort).findByEmail(EMAIL);
    }

    @Test
    void shouldSaveNewOtpWithZeroAttempts() {
        UserDto user = new UserDto(USER_UUID, EMAIL, "hashed", false, RolEnum.STUDENT);
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        useCase.resendOtp(EMAIL);

        ArgumentCaptor<OtpCache> captor = ArgumentCaptor.forClass(OtpCache.class);
        verify(otpRedisRepository).save(captor.capture());
        assertFalse(captor.getValue().isUsed());
        assertEquals(0, captor.getValue().getAttempts());
    }
}
