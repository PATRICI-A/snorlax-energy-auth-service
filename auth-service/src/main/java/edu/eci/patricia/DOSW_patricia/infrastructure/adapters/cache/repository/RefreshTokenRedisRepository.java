package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.RefreshTokenCache;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshTokenCache, String> {

    Optional<RefreshTokenCache> findByUserId(String userId);
}
