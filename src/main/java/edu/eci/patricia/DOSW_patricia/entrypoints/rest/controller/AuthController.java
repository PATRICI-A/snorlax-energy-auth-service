package edu.eci.patricia.DOSW_patricia.entrypoints.rest.controller;

import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.RegisterResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ForgotPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LoginPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LogoutPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.RefreshTokenPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.RegisterUserPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResendOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResetPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ValidateOtpPort;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper.AuthRestMapper;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ForgotPasswordRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.LoginRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.RefreshTokenRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.RegisterRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ResendOtpRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ResetPasswordRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ValidateOtpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Authentication", description = "User registration, OTP verification, login, token refresh, logout and password recovery")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserPort registerUserPort;
    private final ValidateOtpPort validateOtpPort;
    private final LoginPort loginPort;
    private final RefreshTokenPort refreshTokenPort;
    private final LogoutPort logoutPort;
    private final ForgotPasswordPort forgotPasswordPort;
    private final ResetPasswordPort resetPasswordPort;
    private final ResendOtpPort resendOtpPort;
    private final AuthRestMapper mapper;

    @Operation(
            summary = "Register a new user",
            description = "Validates that the email belongs to @mail.escuelaing.edu.co, hashes the password " +
                    "with BCrypt, creates the account (unverified), generates a 6-digit OTP via SecureRandom, " +
                    "and sends it to the institutional email. The account cannot log in until the OTP is verified. " +
                    "NOTE: this endpoint must be called over HTTPS — the password travels in the request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created and OTP sent to institutional email"),
            @ApiResponse(responseCode = "400", description = "Email domain is not @mail.escuelaing.edu.co, or a required field is missing/invalid"),
            @ApiResponse(responseCode = "409", description = "An account with this email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequest request) {
        registerUserPort.register(mapper.toRegisterDto(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDto("OTP sent to email"));
    }

    @Operation(
            summary = "Verify OTP and activate account",
            description = "Validates the 6-digit OTP sent during registration. " +
                    "On success the account is marked as verified and JWT access + refresh tokens are returned. " +
                    "The OTP expires after 10 minutes. After 3 wrong attempts the OTP is blocked — use /resend-otp to get a new one.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP valid — account activated, tokens returned"),
            @ApiResponse(responseCode = "422", description = "OTP invalid, expired, or already used"),
            @ApiResponse(responseCode = "429", description = "Maximum OTP attempts reached — request a new code via /resend-otp")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponseDto> verifyOtp(@Valid @RequestBody ValidateOtpRequest request) {
        return ResponseEntity.ok(validateOtpPort.validateOtp(mapper.toValidateOtpDto(request)));
    }

    @Operation(
            summary = "Resend OTP",
            description = "Generates and sends a new 6-digit OTP to the registered institutional email. " +
                    "Use this when the previous OTP has expired or the maximum number of attempts (3) was reached. " +
                    "Resets the attempt counter. If the account is already verified this call is a no-op.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New OTP sent to email (or account was already verified)"),
            @ApiResponse(responseCode = "422", description = "No account found for the given email")
    })
    @PostMapping("/resend-otp")
    public ResponseEntity<RegisterResponseDto> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        resendOtpPort.resendOtp(request.getEmail());
        return ResponseEntity.ok(new RegisterResponseDto("New OTP sent to email"));
    }

    @Operation(
            summary = "Login",
            description = "Authenticates the user with email and password. " +
                    "Returns a short-lived JWT access token and a long-lived refresh token (7 days). " +
                    "The account must be verified via OTP before login is allowed. " +
                    "After 5 consecutive failed attempts the account is locked for 30 minutes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful — access and refresh tokens returned"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "403", description = "Account exists but email has not been verified yet"),
            @ApiResponse(responseCode = "422", description = "Account is temporarily locked due to repeated failed attempts")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginPort.login(mapper.toLoginDto(request)));
    }

    @Operation(
            summary = "Refresh access token",
            description = "Exchanges a valid refresh token for a new access token. " +
                    "Implements rotation: the old refresh token is invalidated and a new pair is returned.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New access and refresh tokens issued"),
            @ApiResponse(responseCode = "401", description = "Refresh token is invalid or has expired")
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenPort.refresh(request.getRefreshToken()));
    }

    @Operation(
            summary = "Logout",
            description = "Invalidates the current session by revoking the refresh token associated with the Bearer token. " +
                    "After logout the access token remains technically valid until its expiry, " +
                    "but the refresh token can no longer be used to obtain new tokens.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session closed — refresh token revoked"),
            @ApiResponse(responseCode = "401", description = "Bearer token is missing, invalid, or already revoked")
    })
    @PostMapping("/logout")
    public ResponseEntity<RegisterResponseDto> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        logoutPort.logout(token);
        return ResponseEntity.ok(new RegisterResponseDto("Session closed successfully"));
    }

    @Operation(
            summary = "Forgot password",
            description = "Sends a 6-digit recovery code to the institutional email associated with the account. " +
                    "The code expires after 10 minutes and must be used with /reset-password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recovery code sent to email"),
            @ApiResponse(responseCode = "422", description = "No account found for the given email")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<RegisterResponseDto> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordPort.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new RegisterResponseDto("Recovery code sent to email"));
    }

    @Operation(
            summary = "Reset password",
            description = "Validates the recovery code sent by /forgot-password and sets a new password. " +
                    "The new password is hashed with BCrypt before storage. " +
                    "The recovery code is single-use and expires after 10 minutes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "422", description = "Recovery code is invalid, already used, or expired")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<RegisterResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordPort.resetPassword(mapper.toResetPasswordDto(request));
        return ResponseEntity.ok(new RegisterResponseDto("Password updated successfully"));
    }
}
