package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    void shouldCreatePasswordWithValidLength() {
        Password password = new Password("password123");
        assertEquals("password123", password.getValue());
    }

    @Test
    void shouldCreatePasswordWithExactMinLength() {
        Password password = new Password("12345678");
        assertEquals("12345678", password.getValue());
    }

    @Test
    void shouldThrowExceptionForShortPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Password("short"));
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Password(null));
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Password(""));
    }

    @Test
    void shouldThrowExceptionForSevenCharPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Password("1234567"));
    }
}
