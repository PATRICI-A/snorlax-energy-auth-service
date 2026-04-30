package edu.eci.patricia.DOSW_patricia.domain.model;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Email;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private Email email;
    private String hashedPassword;
    private String name;
    private String lastName;
    private String programa;
    private Integer semestre;
    private RolEnum rol;
    private boolean verified;
    private Integer failedAttempts;
    private LocalDateTime blockedUntil;
    private OtpEmbedded otp;

    public User(UUID id, Email email, String hashedPassword, String name, String lastName,
                String programa, Integer semestre, RolEnum rol, boolean verified,
                Integer failedAttempts, LocalDateTime blockedUntil, OtpEmbedded otp) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.lastName = lastName;
        this.programa = programa;
        this.semestre = semestre;
        this.rol = rol;
        this.verified = verified;
        this.failedAttempts = failedAttempts != null ? failedAttempts : 0;
        this.blockedUntil = blockedUntil;
        this.otp = otp;
    }

    public void verify() {
        this.verified = true;
    }

    public void incrementFailedAttempts() {
        this.failedAttempts = (this.failedAttempts == null ? 0 : this.failedAttempts) + 1;
    }

    public void lockAccount(LocalDateTime until) {
        this.blockedUntil = until;
    }

    public void resetLockout() {
        this.failedAttempts = 0;
        this.blockedUntil = null;
    }

    public UUID getId() { return id; }
    public Email getEmail() { return email; }
    public String getHashedPassword() { return hashedPassword; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getPrograma() { return programa; }
    public Integer getSemestre() { return semestre; }
    public RolEnum getRol() { return rol; }
    public boolean isVerified() { return verified; }
    public Integer getFailedAttempts() { return failedAttempts; }
    public LocalDateTime getBlockedUntil() { return blockedUntil; }
    public OtpEmbedded getOtp() { return otp; }

    public void setOtp(OtpEmbedded otp) { this.otp = otp; }
    public void setFailedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; }
    public void setBlockedUntil(LocalDateTime blockedUntil) { this.blockedUntil = blockedUntil; }
}
