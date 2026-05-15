package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the token-refresh endpoint ({@code POST /api/v1/auth/refresh}).
 * Carries the long-lived refresh token to exchange for a new access/refresh token pair.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
