package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.UserServiceNotAvailableException;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
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
                    .uri(userServiceUrl + "/api/v1/internal/users/email/{email}", email)
                    .retrieve()
                    .body(UserDto.class);
            return Optional.ofNullable(user);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (ResourceAccessException | HttpServerErrorException | HttpClientErrorException e) {
            throw new UserServiceNotAvailableException("User service is unavailable: " + e.getMessage());
        }
    }

    @Override
    public Optional<UserDto> findById(String userId) {
        try {
            UserDto user = restClient.get()
                    .uri(userServiceUrl + "/api/v1/internal/users/{userId}", userId)
                    .retrieve()
                    .body(UserDto.class);
            return Optional.ofNullable(user);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (ResourceAccessException | HttpServerErrorException | HttpClientErrorException e) {
            throw new UserServiceNotAvailableException("User service is unavailable: " + e.getMessage());
        }
    }

    @Override
    public void markUserAsVerified(String email) {
        try {
            restClient.patch()
                    .uri(userServiceUrl + "/api/v1/internal/users/email/{email}/verify", email)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException | HttpServerErrorException | HttpClientErrorException e) {
            throw new UserServiceNotAvailableException("User service is unavailable: " + e.getMessage());
        }
    }

    @Override
    public void updatePassword(String email, String newHashedPassword) {
        try {
            restClient.patch()
                    .uri(userServiceUrl + "/api/v1/internal/users/email/{email}/password", email)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("hashedPassword", newHashedPassword))
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException | HttpServerErrorException | HttpClientErrorException e) {
            throw new UserServiceNotAvailableException("User service is unavailable: " + e.getMessage());
        }
    }
}
