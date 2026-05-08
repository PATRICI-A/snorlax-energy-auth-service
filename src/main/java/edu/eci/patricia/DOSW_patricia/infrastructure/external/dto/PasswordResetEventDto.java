package edu.eci.patricia.DOSW_patricia.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEventDto {
    private String email;
    private String resetCode;
    private String userId;
}
