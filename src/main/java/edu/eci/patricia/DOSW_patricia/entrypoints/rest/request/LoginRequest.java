package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the login endpoint ({@code POST /api/v1/auth/login}).
 * Carries the user's institutional email and raw password for authentication.
 */
@Schema(description = "Credentials required to authenticate a registered and email-verified user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Schema(description = "Institutional email address used during registration",
            example = "juan.perez@mail.escuelaing.edu.co")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "Account password (case-sensitive, minimum 8 characters)",
            example = "MiClave123!")
    private String password;
}
