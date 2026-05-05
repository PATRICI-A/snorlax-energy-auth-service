package edu.eci.patricia.DOSW_patricia.entrypoints.rest.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.eci.patricia.DOSW_patricia.application.dto.request.*;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.*;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.*;
import edu.eci.patricia.DOSW_patricia.entrypoints.advice.GlobalExceptionHandler;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper.AuthRestMapper;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private RegisterUserPort registerUserPort;
    @Mock
    private ValidateOtpPort validateOtpPort;
    @Mock
    private LoginPort loginPort;
    @Mock
    private RefreshTokenPort refreshTokenPort;
    @Mock
    private LogoutPort logoutPort;
    @Mock
    private ForgotPasswordPort forgotPasswordPort;
    @Mock
    private ResetPasswordPort resetPasswordPort;
    @Mock
    private ResendOtpPort resendOtpPort;
    @Mock
    private AuthRestMapper mapper;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LoginResponseDto loginResponse;

    private static final String REGISTER_JSON = """
            {
              "email": "student@mail.escuelaing.edu.co",
              "password": "password123",
              "name": "John",
              "lastName": "Doe",
              "program": "CS",
              "semester": 4,
              "interests": ["MUSIC", "PROGRAMMING", "PHOTOGRAPHY"],
              "bio": "bio",
              "birthDate": "2000-01-01",
              "gender": "MALE",
              "profileVisibility": "PUBLIC"
            }
            """;

    private static final String RESET_PASSWORD_JSON = """
            {
              "email": "student@mail.escuelaing.edu.co",
              "code": "123456",
              "newPassword": "newPassword123",
              "confirmPassword": "newPassword123"
            }
            """;

    private static final String RESET_PASSWORD_WRONG_CODE_JSON = """
            {
              "email": "student@mail.escuelaing.edu.co",
              "code": "000000",
              "newPassword": "newPassword123",
              "confirmPassword": "newPassword123"
            }
            """;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        loginResponse = LoginResponseDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .build();
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @Test
    void registerShouldReturn201WhenValid() throws Exception {
        when(mapper.toRegisterDto(any())).thenReturn(RegisterRequestDto.builder()
                .email("student@mail.escuelaing.edu.co").password("password123").build());
        doNothing().when(registerUserPort).register(any());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("OTP sent to email"));
    }

    @Test
    void registerShouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(mapper.toRegisterDto(any())).thenReturn(RegisterRequestDto.builder().build());
        doThrow(new UserAlreadyExistsException("Email taken"))
                .when(registerUserPort).register(any());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("USER_ALREADY_EXISTS"));
    }

    @Test
    void registerShouldReturn400WhenInvalidEmailDomain() throws Exception {
        when(mapper.toRegisterDto(any())).thenReturn(RegisterRequestDto.builder().build());
        doThrow(new InvalidEmailDomainException("Bad domain"))
                .when(registerUserPort).register(any());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("INVALID_EMAIL_DOMAIN"));
    }

    // ── Verify OTP ────────────────────────────────────────────────────────────

    @Test
    void verifyOtpShouldReturn200WhenValid() throws Exception {
        ValidateOtpRequest request = new ValidateOtpRequest(
                "student@mail.escuelaing.edu.co", "123456");
        when(mapper.toValidateOtpDto(any())).thenReturn(ValidateOtpRequestDto.builder()
                .email("student@mail.escuelaing.edu.co").otp("123456").build());
        when(validateOtpPort.validateOtp(any())).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void verifyOtpShouldReturn422WhenOtpExpired() throws Exception {
        ValidateOtpRequest request = new ValidateOtpRequest(
                "student@mail.escuelaing.edu.co", "123456");
        when(mapper.toValidateOtpDto(any())).thenReturn(ValidateOtpRequestDto.builder().build());
        when(validateOtpPort.validateOtp(any()))
                .thenThrow(new OtpExpiredException("OTP expired"));

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.codigo").value("OTP_EXPIRED"));
    }

    @Test
    void verifyOtpShouldReturn422WhenOtpInvalid() throws Exception {
        ValidateOtpRequest request = new ValidateOtpRequest(
                "student@mail.escuelaing.edu.co", "123456");
        when(mapper.toValidateOtpDto(any())).thenReturn(ValidateOtpRequestDto.builder().build());
        when(validateOtpPort.validateOtp(any()))
                .thenThrow(new OtpInvalidException("OTP invalid"));

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.codigo").value("OTP_INVALID"));
    }

    @Test
    void verifyOtpShouldReturn429WhenMaxAttemptsReached() throws Exception {
        ValidateOtpRequest request = new ValidateOtpRequest(
                "student@mail.escuelaing.edu.co", "999999");
        when(mapper.toValidateOtpDto(any())).thenReturn(ValidateOtpRequestDto.builder().build());
        when(validateOtpPort.validateOtp(any()))
                .thenThrow(new OtpMaxAttemptsException("Maximum OTP attempts reached"));

        mockMvc.perform(post("/api/v1/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.codigo").value("OTP_MAX_ATTEMPTS"));
    }

    // ── Resend OTP ────────────────────────────────────────────────────────────

    @Test
    void resendOtpShouldReturn200WhenValid() throws Exception {
        ResendOtpRequest request = new ResendOtpRequest("student@mail.escuelaing.edu.co");
        doNothing().when(resendOtpPort).resendOtp(anyString());

        mockMvc.perform(post("/api/v1/auth/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("New OTP sent to email"));
    }

    @Test
    void resendOtpShouldReturn422WhenUserNotFound() throws Exception {
        ResendOtpRequest request = new ResendOtpRequest("notfound@mail.escuelaing.edu.co");
        doThrow(new OtpInvalidException("No account found"))
                .when(resendOtpPort).resendOtp(anyString());

        mockMvc.perform(post("/api/v1/auth/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.codigo").value("OTP_INVALID"));
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    void loginShouldReturn200WhenValid() throws Exception {
        LoginRequest request = new LoginRequest("student@mail.escuelaing.edu.co", "password123");
        when(mapper.toLoginDto(any())).thenReturn(LoginRequestDto.builder()
                .email("student@mail.escuelaing.edu.co").password("password123").build());
        when(loginPort.login(any())).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void loginShouldReturn401WhenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("student@mail.escuelaing.edu.co", "wrongpass");
        when(mapper.toLoginDto(any())).thenReturn(LoginRequestDto.builder().build());
        when(loginPort.login(any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("INVALID_CREDENTIALS"));
    }

    @Test
    void loginShouldReturn403WhenEmailNotVerified() throws Exception {
        LoginRequest request = new LoginRequest("student@mail.escuelaing.edu.co", "password123");
        when(mapper.toLoginDto(any())).thenReturn(LoginRequestDto.builder().build());
        when(loginPort.login(any())).thenThrow(new EmailNotVerifiedException("Not verified"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("EMAIL_NOT_VERIFIED"));
    }

    @Test
    void loginShouldReturn422WhenAccountLocked() throws Exception {
        LoginRequest request = new LoginRequest("student@mail.escuelaing.edu.co", "password123");
        when(mapper.toLoginDto(any())).thenReturn(LoginRequestDto.builder().build());
        when(loginPort.login(any())).thenThrow(new CuentaBloqueadaException("Locked"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.codigo").value("ACCOUNT_LOCKED"));
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    @Test
    void refreshShouldReturn200WhenValid() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(refreshTokenPort.refresh(anyString())).thenReturn(loginResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void refreshShouldReturn401WhenTokenInvalid() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("bad-token");
        when(refreshTokenPort.refresh(anyString()))
                .thenThrow(new TokenInvalidException("Invalid"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("TOKEN_INVALID"));
    }

    @Test
    void refreshShouldReturn401WhenTokenExpired() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("expired-token");
        when(refreshTokenPort.refresh(anyString()))
                .thenThrow(new TokenExpiredException("Expired"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("TOKEN_EXPIRED"));
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @Test
    void logoutShouldReturn200WhenValid() throws Exception {
        doNothing().when(logoutPort).logout(anyString());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Session closed successfully"));
    }

    // ── Forgot Password ───────────────────────────────────────────────────────

    @Test
    void forgotPasswordShouldReturn200WhenValid() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest("student@mail.escuelaing.edu.co");
        doNothing().when(forgotPasswordPort).forgotPassword(anyString());

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recovery code sent to email"));
    }

    @Test
    void forgotPasswordShouldReturn422WhenEmailNotFound() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest("notfound@example.com");
        doThrow(new OtpInvalidException("Not found"))
                .when(forgotPasswordPort).forgotPassword(anyString());

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.codigo").value("OTP_INVALID"));
    }

    // ── Reset Password ────────────────────────────────────────────────────────

    @Test
    void resetPasswordShouldReturn200WhenValid() throws Exception {
        when(mapper.toResetPasswordDto(any())).thenReturn(ResetPasswordRequestDto.builder()
                .email("student@mail.escuelaing.edu.co").code("123456")
                .newPassword("newPassword123").build());
        doNothing().when(resetPasswordPort).resetPassword(any());

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESET_PASSWORD_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"));
    }

    @Test
    void resetPasswordShouldReturn422WhenCodeInvalid() throws Exception {
        when(mapper.toResetPasswordDto(any())).thenReturn(ResetPasswordRequestDto.builder().build());
        doThrow(new OtpInvalidException("Invalid code"))
                .when(resetPasswordPort).resetPassword(any());

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESET_PASSWORD_WRONG_CODE_JSON))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.codigo").value("OTP_INVALID"));
    }
}
