package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.PasswordResetOtpCache;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data Redis repository for {@link PasswordResetOtpCache}.
 * Provides CRUD operations for password-reset OTP entries keyed by email.
 */
public interface PasswordResetOtpRedisRepository extends CrudRepository<PasswordResetOtpCache, String> {
}
