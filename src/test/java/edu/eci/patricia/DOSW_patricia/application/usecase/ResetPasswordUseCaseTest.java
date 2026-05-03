package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ResetPasswordUseCase resetPasswordUseCase;

    private User user;
    private ResetPasswordRequestDto dto;

    @BeforeEach
    void setUp() {
        OtpEmbedded resetOtp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        user = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John", "Doe", "CS", 4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.MALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, true, 0, null, null
        );
        user.setPasswordResetOtp(resetOtp);

        dto = ResetPasswordRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .code("123456")
                .newPassword("newPassword123")
                .build();
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newHashedPassword");

        assertDoesNotThrow(() -> resetPasswordUseCase.resetPassword(dto));

        verify(userRepository).save(user);
        assertEquals("newHashedPassword", user.getHashedPassword());
        assertTrue(user.getPasswordResetOtp().getUsado());
    }

    @Test
    void shouldThrowOtpInvalidWhenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(OtpInvalidException.class, () -> resetPasswordUseCase.resetPassword(dto));
    }

    @Test
    void shouldThrowOtpInvalidWhenNoResetOtpForUser() {
        user.setPasswordResetOtp(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpInvalidException.class, () -> resetPasswordUseCase.resetPassword(dto));
    }

    @Test
    void shouldThrowOtpExpiredWhenCodeExpired() {
        user.setPasswordResetOtp(new OtpEmbedded("123456", LocalDateTime.now().minusMinutes(1)));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpExpiredException.class, () -> resetPasswordUseCase.resetPassword(dto));
    }

    @Test
    void shouldThrowOtpInvalidWhenCodeAlreadyUsed() {
        user.getPasswordResetOtp().marcaUsado();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpInvalidException.class, () -> resetPasswordUseCase.resetPassword(dto));
    }

    @Test
    void shouldThrowOtpInvalidWhenCodeDoesNotMatch() {
        dto = ResetPasswordRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .code("999999")
                .newPassword("newPassword123")
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpInvalidException.class, () -> resetPasswordUseCase.resetPassword(dto));
    }
}
