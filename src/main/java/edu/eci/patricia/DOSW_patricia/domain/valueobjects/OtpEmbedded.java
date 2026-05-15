package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import java.time.LocalDateTime;

/**
 * Value object encapsulating an OTP code together with its expiry, usage state,
 * and attempt counter. Used both for registration OTPs and password-reset codes.
 */
public class OtpEmbedded {

    private static final int MAX_ATTEMPTS = 3;

    private String codigo;
    private LocalDateTime expiraEn;
    private Boolean usado;
    private Integer intentos;

    /**
     * Creates a fresh, unused OTP with zero attempts.
     *
     * @param codigo   the 6-digit code
     * @param expiraEn the expiry timestamp (typically 10 minutes from now)
     */
    public OtpEmbedded(String codigo, LocalDateTime expiraEn) {
        this.codigo = codigo;
        this.expiraEn = expiraEn;
        this.usado = false;
        this.intentos = 0;
    }

    /** @return {@code true} if the OTP has not been used and has not yet expired */
    public boolean esValido() {
        return !Boolean.TRUE.equals(usado) && expiraEn.isAfter(LocalDateTime.now());
    }

    /** @return {@code true} if the expiry timestamp is in the past */
    public boolean haExpirado() {
        return expiraEn.isBefore(LocalDateTime.now());
    }

    /** Marks this OTP as consumed so it cannot be reused. */
    public void marcaUsado() {
        this.usado = true;
    }

    /** Increments the failed-attempt counter by one. */
    public void incrementarIntentos() {
        this.intentos = (this.intentos == null ? 0 : this.intentos) + 1;
    }

    /** @return {@code true} if the attempt count has reached or exceeded {@value MAX_ATTEMPTS} */
    public boolean haAlcanzadoLimite() {
        return intentos != null && intentos >= MAX_ATTEMPTS;
    }

    public String getCodigo() { return codigo; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public Boolean getUsado() { return usado; }
    public Integer getIntentos() { return intentos; }

    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setExpiraEn(LocalDateTime expiraEn) { this.expiraEn = expiraEn; }
    public void setUsado(Boolean usado) { this.usado = usado; }
    public void setIntentos(Integer intentos) { this.intentos = intentos; }
}
