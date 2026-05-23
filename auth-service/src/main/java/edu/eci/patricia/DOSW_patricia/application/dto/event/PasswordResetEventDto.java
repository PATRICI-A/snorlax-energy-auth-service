package edu.eci.patricia.DOSW_patricia.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEventDto {
    private UUID userId;
    private String email;
    private String resetCode;
}
