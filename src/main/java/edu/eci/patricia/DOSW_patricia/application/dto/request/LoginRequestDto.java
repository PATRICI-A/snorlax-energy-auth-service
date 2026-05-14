package edu.eci.patricia.DOSW_patricia.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Application-layer DTO for the login use case.
 * Carries the user's institutional email and raw password for authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /** The user's institutional email address. */
    private String email;
    /** The user's raw password (never stored — compared against the BCrypt hash). */
    private String password;
}
