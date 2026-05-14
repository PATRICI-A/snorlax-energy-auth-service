package edu.eci.patricia.DOSW_patricia.entrypoints.advice;

import java.time.LocalDateTime;

/**
 * Structured error body returned by {@link GlobalExceptionHandler} for all error responses.
 * Contains a machine-readable error code, a human-readable message, a timestamp, and an
 * optional detail string for validation errors.
 */
public record ErrorResponse(
        String codigo,
        String mensaje,
        LocalDateTime timestamp,
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
