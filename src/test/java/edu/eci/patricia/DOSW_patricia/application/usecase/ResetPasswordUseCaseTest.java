package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.PasswordResetOtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.PasswordResetOtpRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseTest {

    @Mock private UserServicePort userServicePort;
    @Mock private PasswordResetOtpRedisRepository passwordResetOtpRedisRepository;
    @InjectMocks private ResetPasswordUseCase useCase;

    private static final UUID USER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String CODE = "123456";

    private UserDto user;
    private PasswordResetOtpCache validCache;

    @BeforeEach
    void setUp() {
        user = new UserDto(USER_UUID, EMAIL, "hashed", true, "STUDENT");
        validCache = PasswordResetOtpCache.builder().email(EMAIL).code(CODE).used(false).build();
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(validCache));

        useCase.resetPassword(new ResetPasswordRequestDto(EMAIL, CODE, "NewPass123!"));

        verify(userServicePort).updatePassword(USER_UUID.toString(), "NewPass123!");
        verify(passwordResetOtpRedisRepository).save(argThat(PasswordResetOtpCache::isUsed));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(OtpInvalidException.class, () ->
                useCase.resetPassword(new ResetPasswordRequestDto(EMAIL, CODE, "NewPass123!")));
    }

    @Test
    void shouldThrowWhenResetCodeExpired() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());

        assertThrows(OtpExpiredException.class, () ->
                useCase.resetPassword(new ResetPasswordRequestDto(EMAIL, CODE, "NewPass123!")));
    }

    @Test
    void shouldThrowWhenCodeAlreadyUsed() {
        PasswordResetOtpCache usedCache = PasswordResetOtpCache.builder().email(EMAIL).code(CODE).used(true).build();
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(usedCache));

        assertThrows(OtpInvalidException.class, () ->
                useCase.resetPassword(new ResetPasswordRequestDto(EMAIL, CODE, "NewPass123!")));
    }

    @Test
    void shouldThrowWhenCodeIsInvalid() {
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordResetOtpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(validCache));

        assertThrows(OtpInvalidException.class, () ->
                useCase.resetPassword(new ResetPasswordRequestDto(EMAIL, "000000", "NewPass123!")));
    }
}
