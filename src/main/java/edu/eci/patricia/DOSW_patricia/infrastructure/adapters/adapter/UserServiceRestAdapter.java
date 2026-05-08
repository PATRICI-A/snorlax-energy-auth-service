package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceRestAdapter implements UserServicePort {

    private final RestClient restClient;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Override
    public Optional<UserDto> findByEmail(String email) {
        try {
            UserDto user = restClient.get()
                    .uri(userServiceUrl + "/api/users/email/{email}", email)
                    .retrieve()
                    .body(UserDto.class);
            return Optional.ofNullable(user);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDto> findById(String userId) {
        try {
            UserDto user = restClient.get()
                    .uri(userServiceUrl + "/api/users/{userId}", userId)
                    .retrieve()
                    .body(UserDto.class);
            return Optional.ofNullable(user);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public void markUserAsVerified(String userId) {
        restClient.patch()
                .uri(userServiceUrl + "/api/users/{userId}/verify", userId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void updatePassword(String userId, String newHashedPassword) {
        restClient.patch()
                .uri(userServiceUrl + "/api/users/{userId}/password", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("hashedPassword", newHashedPassword))
                .retrieve()
                .toBodilessEntity();
    }
}