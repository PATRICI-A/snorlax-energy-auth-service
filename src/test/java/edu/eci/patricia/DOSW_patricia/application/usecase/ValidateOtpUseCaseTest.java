package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.*;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
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
class ValidateOtpUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ValidateOtpUseCase validateOtpUseCase;

    private User user;
    private ValidateOtpRequestDto dto;

    @BeforeEach
    void setUp() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        user = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John", "Doe", "CS", 4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                null, LocalDate.of(2000, 1, 1), Genero.MALE, ProfileVisibility.PUBLIC,
                RolEnum.STUDENT, false, 0, null, otp
        );
        dto = ValidateOtpRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .otp("123456")
                .build();
    }

    @Test
    void shouldValidateOtpSuccessfully() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        RefreshToken savedSession = new RefreshToken("id", user.getId().toString(),
                "access-token", "refresh-token", LocalDateTime.now().plusMinutes(15),
                false, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        when(refreshTokenRepository.save(any())).thenReturn(savedSession);

        LoginResponseDto response = validateOtpUseCase.validateOtp(dto);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertTrue(user.isVerified());
        assertTrue(user.getOtp().getUsado());
        verify(refreshTokenRepository).deleteByUserId(anyString());
    }

    @Test
    void shouldThrowOtpInvalidForInvalidOtpFormat() {
        dto = ValidateOtpRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .otp("abc123")
                .build();
        assertThrows(OtpInvalidException.class, () -> validateOtpUseCase.validateOtp(dto));
    }

    @Test
    void shouldThrowOtpInvalidWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(OtpInvalidException.class, () -> validateOtpUseCase.validateOtp(dto));
    }

    @Test
    void shouldThrowOtpInvalidWhenNoOtpForUser() {
        user.setOtp(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpInvalidException.class, () -> validateOtpUseCase.validateOtp(dto));
    }

    @Test
    void shouldThrowOtpExpiredWhenOtpIsExpired() {
        user.setOtp(new OtpEmbedded("123456", LocalDateTime.now().minusMinutes(1)));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpExpiredException.class, () -> validateOtpUseCase.validateOtp(dto));
    }

    @Test
    void shouldThrowOtpInvalidWhenOtpAlreadyUsed() {
        user.getOtp().marcaUsado();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpInvalidException.class, () -> validateOtpUseCase.validateOtp(dto));
    }

    @Test
    void shouldThrowOtpInvalidWhenOtpCodeDoesNotMatch() {
        dto = ValidateOtpRequestDto.builder()
                .email("student@mail.escuelaing.edu.co")
                .otp("654321")
                .build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(OtpInvalidException.class, () -> validateOtpUseCase.validateOtp(dto));
    }
}
