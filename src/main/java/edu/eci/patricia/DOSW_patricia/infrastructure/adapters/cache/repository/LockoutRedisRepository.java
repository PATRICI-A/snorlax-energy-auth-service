package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.LockoutCache;
import org.springframework.data.repository.CrudRepository;

public interface LockoutRedisRepository extends CrudRepository<LockoutCache, String> {
}
