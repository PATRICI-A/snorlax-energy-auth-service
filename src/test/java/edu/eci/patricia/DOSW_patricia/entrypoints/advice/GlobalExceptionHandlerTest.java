package edu.eci.patricia.DOSW_patricia.entrypoints.advice;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleInvalidEmailDomainException() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidEmailDomain(
                new InvalidEmailDomainException("Invalid domain"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_EMAIL_DOMAIN", response.getBody().codigo());
        assertEquals("Invalid domain", response.getBody().mensaje());
    }

    @Test
    void shouldHandleUserAlreadyExistsException() {
        ResponseEntity<ErrorResponse> response = handler.handleUserAlreadyExists(
                new UserAlreadyExistsException("User exists"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("USER_ALREADY_EXISTS", response.getBody().codigo());
    }

    @Test
    void shouldHandleInvalidCredentialsException() {
        ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(
                new InvalidCredentialsException());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("INVALID_CREDENTIALS", response.getBody().codigo());
    }

    @Test
    void shouldHandleEmailNotVerifiedException() {
        ResponseEntity<ErrorResponse> response = handler.handleEmailNotVerified(
                new EmailNotVerifiedException("Not verified"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("EMAIL_NOT_VERIFIED", response.getBody().codigo());
    }

    @Test
    void shouldHandleTokenExpiredException() {
        ResponseEntity<ErrorResponse> response = handler.handleTokenExpired(
                new TokenExpiredException("Token expired"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("TOKEN_EXPIRED", response.getBody().codigo());
    }

    @Test
    void shouldHandleTokenInvalidException() {
        ResponseEntity<ErrorResponse> response = handler.handleTokenInvalid(
                new TokenInvalidException("Token invalid"));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("TOKEN_INVALID", response.getBody().codigo());
    }

    @Test
    void shouldHandleOtpExpiredException() {
        ResponseEntity<ErrorResponse> response = handler.handleOtpExpired(
                new OtpExpiredException("OTP expired"));
        assertEquals(422, response.getStatusCode().value());
        assertEquals("OTP_EXPIRED", response.getBody().codigo());
    }

    @Test
    void shouldHandleOtpInvalidException() {
        ResponseEntity<ErrorResponse> response = handler.handleOtpInvalid(
                new OtpInvalidException("OTP invalid"));
        assertEquals(422, response.getStatusCode().value());
        assertEquals("OTP_INVALID", response.getBody().codigo());
    }

    @Test
    void shouldHandleCuentaBloqueadaException() {
        ResponseEntity<ErrorResponse> response = handler.handleCuentaBloqueada(
                new CuentaBloqueadaException("Account locked"));
        assertEquals(422, response.getStatusCode().value());
        assertEquals("ACCOUNT_LOCKED", response.getBody().codigo());
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("Bad request"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BAD_REQUEST", response.getBody().codigo());
        assertEquals("Bad request", response.getBody().mensaje());
    }

    @Test
    void shouldHandleMethodArgumentNotValidExceptionWithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("registerRequest", "email", "must be valid");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().codigo());
        assertEquals("email: must be valid", response.getBody().mensaje());
    }

    @Test
    void shouldHandleMethodArgumentNotValidExceptionWithNoFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("VALIDATION_ERROR", response.getBody().codigo());
        assertEquals("Validation failed", response.getBody().mensaje());
    }

    @Test
    void shouldVerifyErrorResponseHasTimestamp() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("test"));
        assertNotNull(response.getBody().timestamp());
    }
}
