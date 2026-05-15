package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the forgot-password endpoint ({@code POST /api/v1/auth/forgot-password}).
 * Triggers generation and delivery of a 6-digit recovery code to the given email.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;
}
