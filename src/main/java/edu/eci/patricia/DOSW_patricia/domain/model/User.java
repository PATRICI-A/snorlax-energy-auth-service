package edu.eci.patricia.DOSW_patricia.domain.model;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Email;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Genero;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Interes;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.ProfileVisibility;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Core domain model representing a registered user.
 * Encapsulates authentication state (verified, lockout) and profile data.
 * This class is never persisted directly — the auth service reads user state
 * from the external profile service via {@link edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort}.
 */
public class User {

    private UUID id;
    private Email email;
    private String hashedPassword;
    private String name;
    private String lastName;
    private String program;
    private Integer semester;
    private List<Interes> interests;
    private String bio;
    private LocalDate birthDate;
    private Genero gender;
    private ProfileVisibility profileVisibility;
    private RolEnum rol;
    private boolean verified;
    private Integer failedAttempts;
    private LocalDateTime blockedUntil;
    private OtpEmbedded otp;
    private OtpEmbedded passwordResetOtp;

    public User(UUID id, Email email, String hashedPassword, String name, String lastName,
                String program, Integer semester, List<Interes> interests, String bio,
                LocalDate birthDate, Genero gender, ProfileVisibility profileVisibility,
                RolEnum rol, boolean verified, Integer failedAttempts,
                LocalDateTime blockedUntil, OtpEmbedded otp) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.name = name;
        this.lastName = lastName;
        this.program = program;
        this.semester = semester;
        this.interests = interests;
        this.bio = bio;
        this.birthDate = birthDate;
        this.gender = gender;
        this.profileVisibility = profileVisibility != null ? profileVisibility : ProfileVisibility.PUBLIC;
        this.rol = rol;
        this.verified = verified;
        this.failedAttempts = failedAttempts != null ? failedAttempts : 0;
        this.blockedUntil = blockedUntil;
        this.otp = otp;
    }

    /** Marks this user's email as verified so they can log in. */
    public void verify() { this.verified = true; }

    /** Increments the consecutive failed-login counter by one. */
    public void incrementFailedAttempts() {
        this.failedAttempts = (this.failedAttempts == null ? 0 : this.failedAttempts) + 1;
    }

    /**
     * Locks the account until the specified timestamp.
     *
     * @param until the date/time after which the account becomes accessible again
     */
    public void lockAccount(LocalDateTime until) { this.blockedUntil = until; }

    /** Resets the failed-attempt counter and removes the lock. */
    public void resetLockout() {
        this.failedAttempts = 0;
        this.blockedUntil = null;
    }

    public UUID getId() { return id; }
    public Email getEmail() { return email; }
    public String getHashedPassword() { return hashedPassword; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getProgram() { return program; }
    public Integer getSemester() { return semester; }
    public List<Interes> getInterests() { return interests; }
    public String getBio() { return bio; }
    public LocalDate getBirthDate() { return birthDate; }
    public Genero getGender() { return gender; }
    public ProfileVisibility getProfileVisibility() { return profileVisibility; }
    public RolEnum getRol() { return rol; }
    public boolean isVerified() { return verified; }
    public Integer getFailedAttempts() { return failedAttempts; }
    public LocalDateTime getBlockedUntil() { return blockedUntil; }
    public OtpEmbedded getOtp() { return otp; }

    public OtpEmbedded getPasswordResetOtp() { return passwordResetOtp; }

    public void setOtp(OtpEmbedded otp) { this.otp = otp; }
    public void setPasswordResetOtp(OtpEmbedded passwordResetOtp) { this.passwordResetOtp = passwordResetOtp; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public void setFailedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; }
    public void setBlockedUntil(LocalDateTime blockedUntil) { this.blockedUntil = blockedUntil; }
}
