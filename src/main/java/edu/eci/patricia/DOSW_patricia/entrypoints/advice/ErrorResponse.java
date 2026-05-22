package edu.eci.patricia.DOSW_patricia.entrypoints.advice;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Structured error body returned by {@link GlobalExceptionHandler} for all error responses.
 * Contains a machine-readable error code, a human-readable message, a timestamp, and an
 * optional detail string for validation errors.
 */
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
    /**
     * Factory method that stamps the current timestamp automatically.
     *
     * @param codigo   machine-readable error code (e.g. {@code "INVALID_CREDENTIALS"})
     * @param mensaje  human-readable error message
     * @param detalle  optional extra detail (e.g. all validation field errors); may be null
     * @return a new {@code ErrorResponse} with {@code timestamp} set to now
     */
    public static ErrorResponse of(String codigo, String mensaje, String detalle) {
        return new ErrorResponse(codigo, mensaje, LocalDateTime.now(), detalle);
    }
}
