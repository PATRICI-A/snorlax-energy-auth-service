package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenTest {

    @Test
    void shouldCreateJwtTokenWithValidValue() {
        JwtToken token = new JwtToken("some.jwt.token");
        assertEquals("some.jwt.token", token.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThrows(IllegalArgumentException.class, () -> new JwtToken(null));
    }

    @Test
    void shouldThrowExceptionForBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new JwtToken("   "));
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        assertThrows(IllegalArgumentException.class, () -> new JwtToken(""));
    }

    @Test
    void shouldEqualTokenWithSameValue() {
        JwtToken t1 = new JwtToken("abc.def.ghi");
        JwtToken t2 = new JwtToken("abc.def.ghi");
        assertEquals(t1, t2);
    }

    @Test
    void shouldNotEqualTokenWithDifferentValue() {
        JwtToken t1 = new JwtToken("abc.def.ghi");
        JwtToken t2 = new JwtToken("xyz.uvw.rst");
        assertNotEquals(t1, t2);
    }
}
