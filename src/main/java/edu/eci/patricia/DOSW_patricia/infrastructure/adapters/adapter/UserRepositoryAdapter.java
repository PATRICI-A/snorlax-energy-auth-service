package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.mapper.UserEntityMapper;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.repository.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserMongoRepository mongoRepository;
    private final UserEntityMapper mapper;

    @Override
    public void save(User user) {
        mongoRepository.save(mapper.toEntity(user));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }
}
