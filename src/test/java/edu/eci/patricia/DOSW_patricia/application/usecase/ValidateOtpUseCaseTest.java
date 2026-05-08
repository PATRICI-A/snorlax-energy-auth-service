package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpMaxAttemptsException;
import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.OtpRedisRepository;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateOtpUseCaseTest {

    @Mock private OtpRedisRepository otpRedisRepository;
    @Mock private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock private UserServicePort userServicePort;
    @Mock private JwtService jwtService;
    @InjectMocks private ValidateOtpUseCase useCase;

    private static final String EMAIL = "user@mail.escuelaing.edu.co";
    private static final String VALID_OTP = "123456";

    private OtpCache validOtpCache;
    private UserDto user;
    private RefreshToken savedToken;

    @BeforeEach
    void setUp() {
        validOtpCache = OtpCache.builder().email(EMAIL).code(VALID_OTP).used(false).attempts(0).build();
        user = new UserDto("user-id", EMAIL, "hashed", false, RolEnum.STUDENT);
        savedToken = new RefreshToken("id", "user-id", EMAIL, "access-token", "refresh-uuid",
                LocalDateTime.now().plusMinutes(15), false, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
    }

    @Test
    void shouldValidateOtpAndReturnTokens() {
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(validOtpCache));
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyString())).thenReturn("access-token");
        when(jwtService.getJwtExpirationTime()).thenReturn(LocalDateTime.now().plusMinutes(15));
        when(refreshTokenRepository.save(any())).thenReturn(savedToken);

        LoginResponseDto result = useCase.validateOtp(
                ValidateOtpRequestDto.builder().email(EMAIL).otp(VALID_OTP).build());

        assertNotNull(result);
        assertEquals("Bearer", result.getTokenType());
        verify(userServicePort).markUserAsVerified("user-id");
        verify(otpRedisRepository).delete(validOtpCache);
    }

    @Test
    void shouldThrowOtpExpiredWhenNotInCache() {
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.empty());

        assertThrows(OtpExpiredException.class, () -> useCase.validateOtp(
                ValidateOtpRequestDto.builder().email(EMAIL).otp(VALID_OTP).build()));
    }

    @Test
    void shouldThrowOtpInvalidWhenAlreadyUsed() {
        OtpCache usedOtp = OtpCache.builder().email(EMAIL).code(VALID_OTP).used(true).attempts(0).build();
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(usedOtp));

        assertThrows(OtpInvalidException.class, () -> useCase.validateOtp(
                ValidateOtpRequestDto.builder().email(EMAIL).otp(VALID_OTP).build()));
    }

    @Test
    void shouldThrowOtpInvalidAndIncrementAttemptsOnWrongCode() {
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(validOtpCache));

        assertThrows(OtpInvalidException.class, () -> useCase.validateOtp(
                ValidateOtpRequestDto.builder().email(EMAIL).otp("999999").build()));
        verify(otpRedisRepository).save(argThat(o -> o.getAttempts() == 1));
    }

    @Test
    void shouldThrowOtpMaxAttemptsAndDeleteOnThirdFailure() {
        OtpCache twoAttempts = OtpCache.builder().email(EMAIL).code(VALID_OTP).used(false).attempts(2).build();
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(twoAttempts));

        assertThrows(OtpMaxAttemptsException.class, () -> useCase.validateOtp(
                ValidateOtpRequestDto.builder().email(EMAIL).otp("999999").build()));
        verify(otpRedisRepository).delete(twoAttempts);
        verify(otpRedisRepository, never()).save(any());
    }

    @Test
    void shouldThrowOtpInvalidWhenUserNotFoundAfterValidation() {
        when(otpRedisRepository.findById(EMAIL)).thenReturn(Optional.of(validOtpCache));
        when(userServicePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(OtpInvalidException.class, () -> useCase.validateOtp(
                ValidateOtpRequestDto.builder().email(EMAIL).otp(VALID_OTP).build()));
    }
}
