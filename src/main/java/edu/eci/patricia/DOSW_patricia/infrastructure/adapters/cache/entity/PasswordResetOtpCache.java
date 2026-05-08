package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "password_reset_otp", timeToLive = 600)
public class PasswordResetOtpCache {

    @Id
    private String email;
    private String code;
    private boolean used;
}
