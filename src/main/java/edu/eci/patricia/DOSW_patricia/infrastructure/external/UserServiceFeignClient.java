package edu.eci.patricia.DOSW_patricia.infrastructure.external;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "user-service", url = "${user.service.url}", dismiss404 = true)
public interface UserServiceFeignClient {

    @GetMapping("/api/v1/internal/users/mail/{email}")
    UserDto findByEmail(@PathVariable("email") String email);

    @GetMapping("/api/v1/internal/users/{userId}")
    UserDto findById(@PathVariable("userId") String userId);

    @PatchMapping("/api/v1/internal/users/{userId}/verify")
    void markUserAsVerified(@PathVariable("userId") String userId);

    @PatchMapping("/api/v1/internal/users/{userId}/password")
    void updatePassword(@PathVariable("userId") String userId, @RequestBody Map<String, String> body);
}
