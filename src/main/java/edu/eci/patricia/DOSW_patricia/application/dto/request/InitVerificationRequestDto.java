package edu.eci.patricia.DOSW_patricia.application.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Application-layer DTO for the init-verification use case.
 * Carries the user's institutional email and the BCrypt-hashed password supplied by the
 * registration service when it creates the account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitVerificationRequestDto {

    /** The user's institutional email address. */
    @Email(message = "Invalid email format")
    private String email;

}
