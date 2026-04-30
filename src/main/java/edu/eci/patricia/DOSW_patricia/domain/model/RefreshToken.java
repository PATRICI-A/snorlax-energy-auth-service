package edu.eci.patricia.DOSW_patricia.domain.model;

import java.time.LocalDateTime;

public class RefreshToken {

    private String id;
    private String userId;
    private String jwt;
    private String refreshToken;
    private LocalDateTime expiraJwt;
    private Boolean revocado;
    private LocalDateTime createdAt;
    private LocalDateTime expiraRefresh;

    public RefreshToken(String id, String userId, String jwt, String refreshToken,
                        LocalDateTime expiraJwt, Boolean revocado,
                        LocalDateTime createdAt, LocalDateTime expiraRefresh) {
        this.id = id;
        this.userId = userId;
        this.jwt = jwt;
        this.refreshToken = refreshToken;
        this.expiraJwt = expiraJwt;
        this.revocado = revocado;
        this.createdAt = createdAt;
        this.expiraRefresh = expiraRefresh;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiraRefresh);
    }

    public void revoke() {
        this.revocado = true;
    }

    public boolean isRevoked() {
        return Boolean.TRUE.equals(revocado);
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getJwt() { return jwt; }
    public String getRefreshToken() { return refreshToken; }
    public LocalDateTime getExpiraJwt() { return expiraJwt; }
    public Boolean getRevocado() { return revocado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiraRefresh() { return expiraRefresh; }

    public void setRevocado(Boolean revocado) { this.revocado = revocado; }
}
