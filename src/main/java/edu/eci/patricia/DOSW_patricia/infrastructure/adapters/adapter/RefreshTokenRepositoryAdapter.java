package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.RefreshTokenCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenRedisRepository redisRepository;

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenCache cache = RefreshTokenCache.builder()
                .refreshToken(token.getRefreshToken())
                .userId(token.getUserId())
                .email(token.getEmail())
                .jwt(token.getJwt())
                .revoked(Boolean.TRUE.equals(token.getRevocado()))
                .createdAt(token.getCreatedAt())
                .expiraRefresh(token.getExpiraRefresh())
                .build();
        redisRepository.save(cache);
        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return redisRepository.findById(refreshToken).map(this::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByUserId(String userId) {
        return redisRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        redisRepository.findByUserId(userId).ifPresent(redisRepository::delete);
    }

    private RefreshToken toDomain(RefreshTokenCache cache) {
        LocalDateTime expiraRefresh = cache.getExpiraRefresh() != null
                ? cache.getExpiraRefresh()
                : LocalDateTime.now().plusDays(7);
        return new RefreshToken(
                cache.getRefreshToken(),
                cache.getUserId(),
                cache.getEmail(),
                cache.getJwt(),
                cache.getRefreshToken(),
                expiraRefresh,
                cache.isRevoked(),
                cache.getCreatedAt(),
                expiraRefresh
        );
    }
}
