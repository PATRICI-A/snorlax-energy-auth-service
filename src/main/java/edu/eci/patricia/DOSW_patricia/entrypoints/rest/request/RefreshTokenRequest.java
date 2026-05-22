package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the token-refresh endpoint ({@code POST /api/v1/auth/refresh}).
 * Carries the long-lived refresh token to exchange for a new access/refresh token pair.
 */
@Schema(description = "Request body to exchange a refresh token for a new access and refresh token pair")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "The refresh token received from a previous login or token refresh response. Valid for 7 days.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiJyZWZyZXNoLTQ1NiJ9.xyz789")
    private String refreshToken;
}
