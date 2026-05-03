package edu.eci.patricia.DOSW_patricia.domain.model;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                UUID.randomUUID(),
                new Email("student@mail.escuelaing.edu.co"),
                "hashedPassword",
                "John",
                "Doe",
                "Systems Engineering",
                4,
                List.of(Interes.MUSIC, Interes.PROGRAMMING, Interes.PHOTOGRAPHY),
                "A bio",
                LocalDate.of(2000, 1, 1),
                Genero.MALE,
                ProfileVisibility.PUBLIC,
                RolEnum.STUDENT,
                false,
                0,
                null,
                null
        );
    }

    @Test
    void shouldDefaultProfileVisibilityToPublicWhenNull() {
        User u = new User(UUID.randomUUID(), new Email("a@mail.escuelaing.edu.co"), "hashed",
                "A", "B", "prog", 1, List.of(), null, LocalDate.of(2000, 1, 1),
                Genero.OTHER, null, RolEnum.STUDENT, false, 0, null, null);
        assertEquals(ProfileVisibility.PUBLIC, u.getProfileVisibility());
    }

    @Test
    void shouldDefaultFailedAttemptsToZeroWhenNull() {
        User u = new User(UUID.randomUUID(), new Email("a@mail.escuelaing.edu.co"), "hashed",
                "A", "B", "prog", 1, List.of(), null, LocalDate.of(2000, 1, 1),
                Genero.OTHER, ProfileVisibility.PRIVATE, RolEnum.STUDENT, false, null, null, null);
        assertEquals(0, u.getFailedAttempts());
    }

    @Test
    void shouldVerifyUser() {
        assertFalse(user.isVerified());
        user.verify();
        assertTrue(user.isVerified());
    }

    @Test
    void shouldIncrementFailedAttempts() {
        assertEquals(0, user.getFailedAttempts());
        user.incrementFailedAttempts();
        assertEquals(1, user.getFailedAttempts());
        user.incrementFailedAttempts();
        assertEquals(2, user.getFailedAttempts());
    }

    @Test
    void shouldIncrementFailedAttemptsFromNullState() {
        user.setFailedAttempts(null);
        user.incrementFailedAttempts();
        assertEquals(1, user.getFailedAttempts());
    }

    @Test
    void shouldLockAccount() {
        LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(30);
        user.lockAccount(lockUntil);
        assertEquals(lockUntil, user.getBlockedUntil());
    }

    @Test
    void shouldResetLockout() {
        user.incrementFailedAttempts();
        user.lockAccount(LocalDateTime.now().plusMinutes(30));
        user.resetLockout();
        assertEquals(0, user.getFailedAttempts());
        assertNull(user.getBlockedUntil());
    }

    @Test
    void shouldSetAndGetOtp() {
        OtpEmbedded otp = new OtpEmbedded("123456", LocalDateTime.now().plusMinutes(10));
        user.setOtp(otp);
        assertEquals(otp, user.getOtp());
    }

    @Test
    void shouldSetAndGetPasswordResetOtp() {
        OtpEmbedded resetOtp = new OtpEmbedded("654321", LocalDateTime.now().plusMinutes(10));
        user.setPasswordResetOtp(resetOtp);
        assertEquals(resetOtp, user.getPasswordResetOtp());
    }

    @Test
    void shouldSetHashedPassword() {
        user.setHashedPassword("newHashedPassword");
        assertEquals("newHashedPassword", user.getHashedPassword());
    }

    @Test
    void shouldSetBlockedUntil() {
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        user.setBlockedUntil(time);
        assertEquals(time, user.getBlockedUntil());
    }

    @Test
    void shouldGetAllFields() {
        assertNotNull(user.getId());
        assertEquals("student@mail.escuelaing.edu.co", user.getEmail().getValue());
        assertEquals("hashedPassword", user.getHashedPassword());
        assertEquals("John", user.getName());
        assertEquals("Doe", user.getLastName());
        assertEquals("Systems Engineering", user.getProgram());
        assertEquals(4, user.getSemester());
        assertEquals(3, user.getInterests().size());
        assertEquals("A bio", user.getBio());
        assertEquals(LocalDate.of(2000, 1, 1), user.getBirthDate());
        assertEquals(Genero.MALE, user.getGender());
        assertEquals(ProfileVisibility.PUBLIC, user.getProfileVisibility());
        assertEquals(RolEnum.STUDENT, user.getRol());
        assertFalse(user.isVerified());
        assertEquals(0, user.getFailedAttempts());
        assertNull(user.getBlockedUntil());
        assertNull(user.getOtp());
        assertNull(user.getPasswordResetOtp());
    }
}
