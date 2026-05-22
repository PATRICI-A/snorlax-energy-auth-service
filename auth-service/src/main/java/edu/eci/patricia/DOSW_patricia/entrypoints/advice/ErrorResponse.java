package edu.eci.patricia.DOSW_patricia.entrypoints.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Standard error response body returned for all 4xx and 5xx responses")
public record ErrorResponse(
        @Schema(description = "Machine-readable error code identifying the error type",
                example = "INVALID_CREDENTIALS")
        String codigo,
        @Schema(description = "Human-readable description of the error",
                example = "Invalid email or password")
        String mensaje,
        @Schema(description = "Timestamp indicating when the error occurred")
        LocalDateTime timestamp,
        @Schema(description = "Additional detail about the error, typically listing all validation field errors. Null when not applicable",
                example = "email: Must be a valid email address, password: Password is required")
        String detalle
) {
    public static ErrorResponse of(String codigo, String mensaje, String detalle) {
        return new ErrorResponse(codigo, mensaje, LocalDateTime.now(), detalle);
    }
}
