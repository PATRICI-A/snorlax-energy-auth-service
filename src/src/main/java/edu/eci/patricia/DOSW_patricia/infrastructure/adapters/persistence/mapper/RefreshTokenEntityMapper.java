package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.DOSW_patricia.domain.model.RefreshToken;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity.RefreshTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenEntityMapper {

    RefreshTokenEntity toEntity(RefreshToken token);

    default RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) return null;
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getJwt(),
                entity.getRefreshToken(),
                entity.getExpiraJwt(),
                entity.getRevocado(),
                entity.getCreatedAt(),
                entity.getExpiraRefresh()
        );
    }
}
