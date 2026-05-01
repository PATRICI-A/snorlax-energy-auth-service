package edu.eci.patricia.DOSW_patricia.entrypoints.rest.controller;

import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.RegisterResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LoginPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LogoutPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.RefreshTokenPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.RegisterUserPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ValidateOtpPort;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper.AuthRestMapper;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.LoginRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.RefreshTokenRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.RegisterRequest;
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


@Tag(name = "Authentication", description = "User registration, OTP verification, login, token refresh and logout")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserPort registerUserPort;
    private final ValidateOtpPort validateOtpPort;
    private final LoginPort loginPort;
    private final RefreshTokenPort refreshTokenPort;
    private final LogoutPort logoutPort;
    private final AuthRestMapper mapper;

    @Operation(summary = "Register a new user", description = "Validates institutional email, saves user, and sends a 6-digit OTP")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "OTP sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email domain or missing fields"),
        @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequest request) {
        registerUserPort.register(mapper.toRegisterDto(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDto("OTP sent to email"));
    }

    @Operation(summary = "Verify OTP code", description = "Validates the OTP, activates the account and returns JWT tokens to complete registration")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account verified — tokens returned"),
        @ApiResponse(responseCode = "422", description = "Invalid or expired OTP")
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponseDto> verifyOtp(@Valid @RequestBody ValidateOtpRequest request) {
        return ResponseEntity.ok(validateOtpPort.validateOtp(mapper.toValidateOtpDto(request)));
    }

    @Operation(summary = "Login", description = "Validates credentials and returns a JWT access token plus refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Email not verified"),
        @ApiResponse(responseCode = "422", description = "Account locked")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginPort.login(mapper.toLoginDto(request)));
    }

    @Operation(summary = "Refresh access token", description = "Exchanges a valid refresh token for a new access token (rotation)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New tokens issued"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenPort.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "Logout", description = "Invalidates the current session by revoking the refresh token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session closed successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or missing token")
    })
    @PostMapping("/logout")
    public ResponseEntity<RegisterResponseDto> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        logoutPort.logout(token);
        return ResponseEntity.ok(new RegisterResponseDto("Session closed successfully"));
    }
}
