package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import java.time.LocalDateTime;

public class OtpEmbedded {

    private static final int MAX_ATTEMPTS = 3;

    private String codigo;
    private LocalDateTime expiraEn;
    private Boolean usado;
    private Integer intentos;

    public OtpEmbedded(String codigo, LocalDateTime expiraEn) {
        this.codigo = codigo;
        this.expiraEn = expiraEn;
        this.usado = false;
        this.intentos = 0;
    }

    public boolean esValido() {
        return !Boolean.TRUE.equals(usado) && expiraEn.isAfter(LocalDateTime.now());
    }

    public boolean haExpirado() {
        return expiraEn.isBefore(LocalDateTime.now());
    }

    public void marcaUsado() {
        this.usado = true;
    }

    public void incrementarIntentos() {
        this.intentos = (this.intentos == null ? 0 : this.intentos) + 1;
    }

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
