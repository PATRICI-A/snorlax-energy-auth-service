package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.RefreshTokenCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Adapter that implements RefreshTokenRepositoryPort using Redis as the backing store.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenRedisRepository redisRepository;

    /**
     * Persists a refresh token session in Redis.
     *
     * @param token the domain refresh token to save
     * @return the saved token
     */
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

    /**
     * Finds a session by its refresh token value.
     *
     * @param refreshToken the refresh token string
     * @return the matching session, or empty if not found
     */
    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return redisRepository.findById(refreshToken).map(this::toDomain);
    }

    /**
     * Finds a session by the user's ID.
     *
     * @param userId the user's ID
     * @return the matching session, or empty if not found
     */
    @Override
    public Optional<RefreshToken> findByUserId(String userId) {
        return redisRepository.findByUserId(userId).map(this::toDomain);
    }

    /**
     * Deletes all sessions associated with the given user.
     *
     * @param userId the user's ID
     */
    @Override
    public void deleteByUserId(String userId) {
        redisRepository.findByUserId(userId).ifPresent(redisRepository::delete);
    }

    /**
     * Maps a Redis cache entity to the domain model.
     *
     * @param cache the Redis cache entity
     * @return the domain RefreshToken
     */
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
