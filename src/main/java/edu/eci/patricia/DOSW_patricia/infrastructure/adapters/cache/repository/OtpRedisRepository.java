package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.OtpCache;
import org.springframework.data.repository.CrudRepository;

public interface OtpRedisRepository extends CrudRepository<OtpCache, String> {
}
