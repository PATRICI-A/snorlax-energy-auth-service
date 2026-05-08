package edu.eci.patricia.DOSW_patricia.domain.ports.out;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByToken(String refreshToken);

    Optional<RefreshToken> findByUserId(String userId);

    void deleteByUserId(String userId);
}
