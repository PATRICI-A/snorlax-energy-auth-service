package edu.eci.patricia.DOSW_patricia.entrypoints.rest.controller;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ChangePasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.LoginResponseDto;
import edu.eci.patricia.DOSW_patricia.application.dto.response.RegisterResponseDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ChangePasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ForgotPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.InitVerificationPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LoginPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.LogoutPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.RefreshTokenPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResendOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResetPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ValidateOtpPort;
import edu.eci.patricia.DOSW_patricia.entrypoints.advice.ErrorResponse;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper.AuthRestMapper;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ChangePasswordRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ForgotPasswordRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.LoginRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.RefreshTokenRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ResendOtpRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ResetPasswordRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ValidateOtpRequest;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller exposing all authentication endpoints under {@code /api/v1/auth}.
 * Delegates every operation to the corresponding use-case port and maps REST requests
 * to application-layer DTOs via {@link AuthRestMapper}.
 */
@Tag(
        name = "Authentication",
        description = "Endpoints for the complete authentication lifecycle: new-user OTP-based email " +
                "verification, credential-based login with brute-force protection, JWT token refresh and " +
                "revocation, and all password management operations (forgot, reset, and authenticated change). " +
                "Tokens are HMAC-SHA256 signed JWTs. Endpoints marked with a lock icon require a valid " +
                "Bearer token in the Authorization header."
)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final InitVerificationPort initVerificationPort;
    private final ValidateOtpPort validateOtpPort;
    private final LoginPort loginPort;
    private final RefreshTokenPort refreshTokenPort;
    private final LogoutPort logoutPort;
    private final ForgotPasswordPort forgotPasswordPort;
    private final ResetPasswordPort resetPasswordPort;
    private final ResendOtpPort resendOtpPort;
    private final ChangePasswordPort changePasswordPort;
    private final AuthRestMapper mapper;
    private final JwtService jwtService;

    @Operation(
            summary = "Initialize OTP verification",
            description = "Triggered by the registration service immediately after a new user account has been " +
                    "created. Receives the user's institutional email and their BCrypt-hashed password, generates " +
                    "a cryptographically random 6-digit OTP, stores it in Redis with a 10-minute TTL, and " +
                    "publishes an email event via RabbitMQ so the notification service delivers the code to the " +
                    "user's inbox. This endpoint does NOT return the OTP — it only confirms the code was " +
                    "dispatched. The user must then call POST /verify-otp to activate their account."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "OTP successfully generated and dispatched to the provided institutional email.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. Possible causes: missing fields, malformed email " +
                            "format, or email domain not accepted (must end in @mail.escuelaing.edu.co).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "A user with the given email already exists in the system.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred. Please retry after a moment.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503",
                    description = "The User Service is temporarily unreachable. This endpoint depends on an " +
                            "external service to validate and store user data. Retry after a short delay.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/init-verification")
    public ResponseEntity<RegisterResponseDto> initVerification(
            @Valid @RequestBody String mail) {
        initVerificationPort.initVerification(mail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDto("OTP sent to email"));
    }

    @Operation(
            summary = "Verify OTP and activate account",
            description = "Validates the 6-digit OTP sent to the user's institutional email during registration. " +
                    "On success: (1) the account is marked as email-verified in the User Service, (2) a " +
                    "short-lived JWT access token (15 min) and a long-lived refresh token (7 days) are returned. " +
                    "The OTP has a 10-minute TTL in Redis. After 3 consecutive wrong attempts the OTP entry is " +
                    "deleted — the user must call POST /resend-otp to receive a fresh code before retrying."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "OTP is valid. Account activated. Access and refresh tokens returned.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. Possible causes: missing email, missing OTP, " +
                            "or OTP does not match the required 6-digit numeric format.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422",
                    description = "OTP is invalid, has already been used, or has expired (10-minute TTL exceeded).",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429",
                    description = "Maximum OTP verification attempts (3) exceeded. The OTP has been invalidated. " +
                            "Call POST /resend-otp to receive a new code.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponseDto> verifyOtp(@Valid @RequestBody ValidateOtpRequest request) {
        return ResponseEntity.ok(validateOtpPort.validateOtp(mapper.toValidateOtpDto(request)));
    }

    @Operation(
            summary = "Resend OTP",
            description = "Generates a new 6-digit OTP and sends it to the institutional email associated with " +
                    "the pending account. Use this endpoint when the previous OTP has expired (after 10 minutes) " +
                    "or when the 3-attempt limit has been exhausted. The old OTP (if still cached) is replaced by " +
                    "the new one. The new code is delivered via RabbitMQ and expires after 10 minutes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "A new OTP has been generated and sent to the provided email address.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. Possible causes: missing email or invalid email format.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422",
                    description = "No pending verification was found for the given email address.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/resend-otp")
    public ResponseEntity<RegisterResponseDto> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        resendOtpPort.resendOtp(request.getEmail());
        return ResponseEntity.ok(new RegisterResponseDto("New OTP sent to email"));
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a registered and email-verified user with their institutional email and " +
                    "password. On success, returns a short-lived JWT access token (15-minute expiry) and a " +
                    "long-lived refresh token (7-day expiry). The password is compared against the BCrypt hash " +
                    "stored in the User Service. After 5 consecutive failed attempts the account is temporarily " +
                    "locked for 30 minutes to prevent brute-force attacks. The access token must be sent in the " +
                    "Authorization header (Bearer scheme) for all protected endpoints."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Authentication successful. Access and refresh tokens returned.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. Possible causes: missing email or password fields.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Invalid credentials — the email does not exist or the password is incorrect.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Account exists but email address has not been verified via OTP yet. " +
                            "Complete verification via POST /verify-otp first.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422",
                    description = "Account is temporarily locked after 5 consecutive failed login attempts. " +
                            "The lock expires automatically after 30 minutes.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginPort.login(mapper.toLoginDto(request)));
    }

    @Operation(
            summary = "Refresh access token",
            description = "Exchanges a valid, non-expired refresh token for a new token pair. Implements token " +
                    "rotation: the submitted refresh token is immediately invalidated in Redis and a new access " +
                    "token (15 min) plus a new refresh token (7 days) are returned. This prevents refresh-token " +
                    "reuse attacks. Clients should store the new refresh token and discard the old one."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Token pair refreshed. New access token and new refresh token returned.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. The refreshToken field is missing or blank.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "The refresh token is invalid, has already been used, or has expired.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenPort.refresh(request.getRefreshToken()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Logout",
            description = "Terminates the current session by extracting the user ID from the Bearer JWT and " +
                    "deleting the associated refresh token from Redis. After logout, the refresh token can no " +
                    "longer generate new access tokens. The access token itself remains technically valid until " +
                    "its natural 15-minute expiry (JWTs are stateless), but will have no corresponding refresh " +
                    "token. Clients must discard both tokens upon receiving a 200 response."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Session closed successfully. Refresh token has been revoked.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Authorization header is missing, malformed, or contains an invalid/expired JWT.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
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
            description = "Initiates the password recovery flow. A 6-digit numeric recovery code is generated, " +
                    "stored in Redis with a 10-minute TTL, and dispatched to the institutional email via " +
                    "RabbitMQ. The code is single-use and expires after 10 minutes. After receiving it, call " +
                    "POST /reset-password with the code and the new password to complete the recovery."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Recovery code generated and sent to the provided email address.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. Possible causes: missing email or invalid email format.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422",
                    description = "No account was found for the provided email address.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<RegisterResponseDto> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        forgotPasswordPort.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new RegisterResponseDto("Recovery code sent to email"));
    }

    @Operation(
            summary = "Reset password",
            description = "Completes the password recovery flow started by POST /forgot-password. Validates the " +
                    "6-digit recovery code against the value stored in Redis for the given email. If valid and " +
                    "not expired, the new password is hashed with BCrypt and updated in the User Service via " +
                    "OpenFeign. The code is single-use and deleted from Redis immediately upon success. " +
                    "Both newPassword and confirmPassword must match. Minimum password length is 8 characters."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Password updated successfully. User may now log in with the new credentials.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. Possible causes: missing fields, password shorter " +
                            "than 8 characters, or newPassword and confirmPassword do not match.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422",
                    description = "The recovery code is invalid, has already been used, or has expired.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<RegisterResponseDto> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordPort.resetPassword(mapper.toResetPasswordDto(request));
        return ResponseEntity.ok(new RegisterResponseDto("Password updated successfully"));
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Change password",
            description = "Allows an authenticated user to change their own password. Requires a valid JWT Bearer " +
                    "token in the Authorization header. The current password is verified against the BCrypt hash " +
                    "stored in the User Service before applying the update. The new password is hashed with " +
                    "BCrypt and saved via OpenFeign. This endpoint requires the user to know their current " +
                    "password — it is not a recovery flow. Use POST /forgot-password for forgotten passwords."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Password changed successfully. Takes effect immediately.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RegisterResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Request validation failed. Possible causes: missing fields or new password " +
                            "shorter than 8 characters.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Authorization header is missing or invalid/expired JWT, or the provided " +
                            "current password does not match the stored hash.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "An unexpected server-side error occurred.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/change-password")
    public ResponseEntity<RegisterResponseDto> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        String token = authHeader.replace("Bearer ", "").trim();
        String userId = jwtService.extractUserId(token);
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto(
                userId, request.getCurrentPassword(), request.getNewPassword());
        changePasswordPort.changePassword(dto);
        return ResponseEntity.ok(new RegisterResponseDto("Password changed successfully"));
    }
}
