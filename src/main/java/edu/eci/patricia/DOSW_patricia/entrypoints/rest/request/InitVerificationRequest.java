package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the init-verification endpoint ({@code POST /api/v1/auth/init-verification}).
 * Sent by the registration service after creating the user account to trigger OTP delivery.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitVerificationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Schema(example = "juan.perez@mail.escuelaing.edu.co")
    private String email;

    @NotBlank(message = "Hashed password is required")
    @Schema(example = "$2a$10$...", description = "BCrypt-hashed password from the registration service")
    private String hashedPassword;
}
