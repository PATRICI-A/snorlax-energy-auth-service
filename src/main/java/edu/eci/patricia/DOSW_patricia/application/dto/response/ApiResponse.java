package edu.eci.patricia.DOSW_patricia.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic wrapper for API responses that need to carry a typed payload.
 * Null fields are omitted from the JSON output via {@code @JsonInclude}.
 *
 * @param <T> the type of the response payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** HTTP status code echoed in the response body. */
    private int status;
    /** Machine-readable error code; null when the request succeeded. */
    private String error;
    /** Human-readable status or error message. */
    private String message;
    /** The response payload; null when an error occurred. */
    private T data;
}
