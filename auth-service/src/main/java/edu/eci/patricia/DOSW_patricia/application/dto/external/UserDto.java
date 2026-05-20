package edu.eci.patricia.DOSW_patricia.application.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;

public record UserDto(
        String id,
        String email,
        @JsonProperty("passwordHash") String hashedPassword,
        boolean verified,
        @JsonProperty("userType") RolEnum rol
) {}
