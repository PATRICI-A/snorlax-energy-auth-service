package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.feign;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

@Headers("Content-Type: application/json")
public interface UserServiceClient {

    @RequestLine("GET /api/v1/internal/users/mail/{email}")
    UserDto findByEmail(@Param("email") String email);

    @RequestLine("GET /api/v1/internal/users/{userId}")
    UserDto findById(@Param("userId") String userId);

    @RequestLine("PATCH /api/v1/internal/users/{userId}/verify")
    void markUserAsVerified(@Param("userId") String userId);

    @RequestLine("PATCH /api/v1/internal/users/{userId}/password")
    void updatePassword(@Param("userId") String userId, Map<String, String> body);
}
