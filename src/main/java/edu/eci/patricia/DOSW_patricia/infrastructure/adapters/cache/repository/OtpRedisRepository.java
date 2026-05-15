package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data Redis repository for {@link OtpCache}.
 * Provides CRUD operations for registration OTP entries keyed by email.
 */
public interface OtpRedisRepository extends CrudRepository<OtpCache, String> {
}
