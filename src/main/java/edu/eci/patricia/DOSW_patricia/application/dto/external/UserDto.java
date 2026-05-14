package edu.eci.patricia.DOSW_patricia.application.dto.external;

import java.util.UUID;

/**
 * Immutable data transfer object representing the subset of user data returned by
 * the external profile service. Only contains the fields the auth service needs.
 */
public record UserDto(
        /** The user's unique identifier. */
        UUID id,
        /** The user's institutional email. */
        String email,
        /** BCrypt-hashed password stored in the profile service. */
        String passwordHash,
        /** Whether the user has completed email verification. */
        boolean verified,
        /** The user's role/type (e.g. {@code "STUDENT"}, {@code "ADMIN"}). */
        String userType
) {}
