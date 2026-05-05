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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResendOtpUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private EmailSenderPort emailSender;

    @InjectMocks
    private ResendOtpUseCase resendOtpUseCase;

    private User unverifiedUser;
    private User verifiedUser;

    @BeforeEach
    void setUp() {
        OtpEmbedded otp = new OtpEmbedded("111111", LocalDateTime.now().minusMinutes(15));
        unverifiedUser = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John", "Doe", "CS", 4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.MALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, false, 0, null, otp
        );
        verifiedUser = new User(
                UUID.randomUUID(),
                new Email("verified@mail.escuelaing.edu.co"),
                "hashedPassword",
                "Jane", "Doe", "CS", 5,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.FEMALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, true, 0, null, null
        );
    }

    @Test
    void shouldResendOtpForUnverifiedUser() {
        when(userRepository.findByEmail("student@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(unverifiedUser));

        assertDoesNotThrow(() -> resendOtpUseCase.resendOtp("student@mail.escuelaing.edu.co"));

        verify(userRepository).save(unverifiedUser);
        verify(emailSender).sendOtp(eq("student@mail.escuelaing.edu.co"), anyString());
        assertNotNull(unverifiedUser.getOtp());
        assertFalse(unverifiedUser.getOtp().haExpirado());
        assertEquals(0, unverifiedUser.getOtp().getIntentos());
    }

    @Test
    void shouldDoNothingWhenUserAlreadyVerified() {
        when(userRepository.findByEmail("verified@mail.escuelaing.edu.co"))
                .thenReturn(Optional.of(verifiedUser));

        assertDoesNotThrow(() -> resendOtpUseCase.resendOtp("verified@mail.escuelaing.edu.co"));

        verify(userRepository, never()).save(any());
        verify(emailSender, never()).sendOtp(anyString(), anyString());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(OtpInvalidException.class,
                () -> resendOtpUseCase.resendOtp("ghost@mail.escuelaing.edu.co"));
    }
}
