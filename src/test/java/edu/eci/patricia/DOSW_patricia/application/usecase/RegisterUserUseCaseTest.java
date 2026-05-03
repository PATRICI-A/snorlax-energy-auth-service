package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.RegisterRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidEmailDomainException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.UserAlreadyExistsException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Genero;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Interes;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.ProfileVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private EmailSenderPort emailSender;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private RegisterRequestDto registerDto;

    @BeforeEach
    void setUp() {
        registerDto = RegisterRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .password("password123")
                .name("John")
                .lastName("Doe")
                .program("Systems Engineering")
                .semester(4)
                .interests(List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY))
                .bio("A student")
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(Genero.MALE)
                .profileVisibility(ProfileVisibility.PUBLIC)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        assertDoesNotThrow(() -> registerUserUseCase.register(registerDto));

        verify(userRepository).save(any());
        verify(emailSender).sendOtp(anyString(), anyString());
    }

    @Test
    void shouldThrowUserAlreadyExistsWhenEmailTaken() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> registerUserUseCase.register(registerDto));
        verify(userRepository, never()).save(any());
        verify(emailSender, never()).sendOtp(anyString(), anyString());
    }

    @Test
    void shouldThrowInvalidEmailDomainForInvalidEmail() {
        RegisterRequestDto invalidDto = RegisterRequestDto.builder()
                .email("student@gmail.com")
                .password("password123")
                .name("John")
                .lastName("Doe")
                .program("CS")
                .semester(1)
                .interests(List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY))
                .bio(null)
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(Genero.MALE)
                .profileVisibility(null)
                .build();
        assertThrows(InvalidEmailDomainException.class, () -> registerUserUseCase.register(invalidDto));
    }

    @Test
    void shouldRegisterUserWithNullProfileVisibility() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .password("password123")
                .name("John")
                .lastName("Doe")
                .program("CS")
                .semester(1)
                .interests(List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY))
                .bio(null)
                .birthDate(LocalDate.of(2000, 1, 1))
                .gender(Genero.MALE)
                .profileVisibility(null)
                .build();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

        assertDoesNotThrow(() -> registerUserUseCase.register(dto));
    }
}
