package edu.eci.patricia.DOSW_patricia.application.dto.external;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;

public record UserDto(
        String id,
        String email,
        String hashedPassword,
        boolean verified,
        RolEnum rol
) {}
