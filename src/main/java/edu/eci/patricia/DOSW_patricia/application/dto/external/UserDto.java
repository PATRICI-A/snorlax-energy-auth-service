package edu.eci.patricia.DOSW_patricia.application.dto.external;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;

import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String hashedPassword,
        boolean verified,
        RolEnum rol
) {}
