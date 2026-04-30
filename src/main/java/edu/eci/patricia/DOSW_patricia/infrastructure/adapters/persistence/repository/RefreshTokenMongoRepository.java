package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity.RefreshTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenMongoRepository extends MongoRepository<RefreshTokenEntity, String> {

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

    Optional<RefreshTokenEntity> findByUserId(String userId);

    void deleteByUserId(String userId);
}
