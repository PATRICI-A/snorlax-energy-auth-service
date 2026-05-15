package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.LockoutCache;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data Redis repository for {@link LockoutCache}.
 * Provides CRUD operations for failed-login lockout entries keyed by email.
 */
public interface LockoutRedisRepository extends CrudRepository<LockoutCache, String> {
}
