package edu.eci.patricia.DOSW_patricia.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple response DTO used for operations that return only a status message
 * (e.g. OTP sent, logout, password changed).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDto {

    /** Human-readable confirmation message. */
    private String message;
}
