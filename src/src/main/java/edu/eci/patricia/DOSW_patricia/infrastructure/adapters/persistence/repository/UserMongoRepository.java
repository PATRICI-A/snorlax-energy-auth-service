package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
