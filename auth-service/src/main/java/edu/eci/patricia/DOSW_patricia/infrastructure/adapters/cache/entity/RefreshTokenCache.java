package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 604800)
public class RefreshTokenCache {

    @Id
    private String refreshToken;

    @Indexed
    private String userId;

    private String email;
    private String jwt;
    private boolean revoked;
    private LocalDateTime createdAt;
    private LocalDateTime expiraJwt;
    private LocalDateTime expiraRefresh;
}
