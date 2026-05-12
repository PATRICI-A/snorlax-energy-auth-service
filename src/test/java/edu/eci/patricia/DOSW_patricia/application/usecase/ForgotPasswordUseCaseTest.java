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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private PasswordResetOtpRedisRepository passwordResetOtpRedisRepository;
    @Mock private EmailSenderPort emailSender;
    @InjectMocks private ForgotPasswordUseCase useCase;

    private static final UUID USER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String EMAIL = "user@mail.escuelaing.edu.co";

    @Test
    void shouldSendResetCodeSuccessfully() {
        UserDto user = new UserDto(USER_UUID, EMAIL, "hashed", true, RolEnum.STUDENT);
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        useCase.forgotPassword(EMAIL);

        verify(passwordResetOtpRedisRepository).save(any(PasswordResetOtpCache.class));
        verify(emailSender).sendPasswordReset(eq(EMAIL), anyString(), eq(USER_UUID));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(OtpInvalidException.class, () -> useCase.forgotPassword(EMAIL));
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        UserDto user = new UserDto(USER_UUID, EMAIL, "hashed", true, RolEnum.STUDENT);
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        useCase.forgotPassword("USER@mail.escuelaing.edu.co");

        verify(userServicePort).findByEmail(EMAIL);
    }

    @Test
    void shouldSaveSixDigitResetCode() {
        UserDto user = new UserDto(USER_UUID, EMAIL, "hashed", true, RolEnum.STUDENT);
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        useCase.forgotPassword(EMAIL);

        ArgumentCaptor<PasswordResetOtpCache> captor = ArgumentCaptor.forClass(PasswordResetOtpCache.class);
        verify(passwordResetOtpRedisRepository).save(captor.capture());
        assertTrue(captor.getValue().getCode().matches("\\d{6}"));
        assertFalse(captor.getValue().isUsed());
    }
}
