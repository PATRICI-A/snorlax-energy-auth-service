package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request body for the change-password endpoint ({@code POST /api/v1/auth/change-password}).
 * The authenticated user's ID is extracted from the Bearer JWT in the Authorization header,
 * not supplied in this body.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Schema(example = "MiClave123!")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    @Schema(example = "NuevaClave456!")
    private String newPassword;
}
