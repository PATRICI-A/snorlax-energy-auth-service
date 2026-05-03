package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private EmailSenderPort emailSender;

    @InjectMocks
    private ForgotPasswordUseCase forgotPasswordUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John", "Doe", "CS", 4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.MALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, true, 0, null, null
        );
    }

    @Test
    void shouldSendPasswordResetOtpSuccessfully() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertDoesNotThrow(() ->
                forgotPasswordUseCase.forgotPassword("student@mail.escuelaing.edu.co"));

        verify(userRepository).save(user);
        verify(emailSender).sendPasswordReset(anyString(), anyString());
        assertNotNull(user.getPasswordResetOtp());
    }

    @Test
    void shouldTrimAndLowercaseEmailBeforeLookup() {
        when(userRepository.findByEmail("student@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow(() ->
                forgotPasswordUseCase.forgotPassword("  STUDENT@mail.escuelaing.edu.co  "));

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowOtpInvalidWhenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(OtpInvalidException.class,
                () -> forgotPasswordUseCase.forgotPassword("notfound@mail.escuelaing.edu.co"));
    }
}
