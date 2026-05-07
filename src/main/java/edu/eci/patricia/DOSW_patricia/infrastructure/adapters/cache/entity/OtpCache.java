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
@RedisHash(value = "otp", timeToLive = 600)
public class OtpCache {

    @Id
    private String email;
    private String code;
    private boolean used;
    private int attempts;
}
