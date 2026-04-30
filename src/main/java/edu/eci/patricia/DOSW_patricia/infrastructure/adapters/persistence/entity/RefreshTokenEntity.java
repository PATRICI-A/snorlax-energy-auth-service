package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "sesiones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenEntity {

    @Id
    private String id;

    private String userId;
    private String jwt;
    private String refreshToken;
    private LocalDateTime expiraJwt;
    private Boolean revocado;
    private LocalDateTime createdAt;
    private LocalDateTime expiraRefresh;
}
