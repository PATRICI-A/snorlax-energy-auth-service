package edu.eci.patricia.DOSW_patricia.domain.ports.out;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;

import java.util.Optional;

public interface UserServicePort {

    Optional<UserDto> findByEmail(String email);

    Optional<UserDto> findById(String userId);

    void markUserAsVerified(String userId);

    void updatePassword(String userId, String newHashedPassword);
}
