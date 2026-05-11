package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.feign.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceRestAdapter implements UserServicePort {

    private final UserServiceClient userServiceClient;

    @Override
    public Optional<UserDto> findByEmail(String email) {
        try {
            return Optional.ofNullable(userServiceClient.findByEmail(email));
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDto> findById(String userId) {
        try {
            return Optional.ofNullable(userServiceClient.findById(userId));
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public void markUserAsVerified(String userId) {
        userServiceClient.markUserAsVerified(userId);
    }

    @Override
    public void updatePassword(String userId, String newHashedPassword) {
        userServiceClient.updatePassword(userId, Map.of("hashedPassword", newHashedPassword));
    }
}
