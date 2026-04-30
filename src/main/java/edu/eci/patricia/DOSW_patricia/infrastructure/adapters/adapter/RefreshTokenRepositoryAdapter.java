package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.RefreshTokenRepositoryPort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.mapper.RefreshTokenEntityMapper;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.repository.RefreshTokenMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenMongoRepository mongoRepository;
    private final RefreshTokenEntityMapper mapper;

    @Override
    public RefreshToken save(RefreshToken token) {
        return mapper.toDomain(mongoRepository.save(mapper.toEntity(token)));
    }

    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return mongoRepository.findByRefreshToken(refreshToken).map(mapper::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        mongoRepository.deleteByUserId(userId);
    }
}
