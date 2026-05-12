package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.external.UserServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceFeignAdapter implements UserServicePort {

    private final UserServiceFeignClient feignClient;

    @Override
    public Optional<UserDto> findByEmail(String email) {
        return Optional.ofNullable(feignClient.findByEmail(email));
    }

    @Override
    public Optional<UserDto> findById(String userId) {
        return Optional.ofNullable(feignClient.findById(userId));
    }

    @Override
    public void markUserAsVerified(String userId) {
        feignClient.markUserAsVerified(userId);
    }

    @Override
    public void updatePassword(String userId, String newHashedPassword) {
        feignClient.updatePassword(userId, Map.of("hashedPassword", newHashedPassword));
    }
}