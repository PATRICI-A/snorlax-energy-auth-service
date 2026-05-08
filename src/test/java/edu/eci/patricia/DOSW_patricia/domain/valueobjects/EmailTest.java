package edu.eci.patricia.DOSW_patricia.domain.valueobjects;

import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidEmailDomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateEmailWithValidDomain() {
        Email email = new Email("student@mail.escuelaing.edu.co");
        assertEquals("student@mail.escuelaing.edu.co", email.getValue());
    }

    @Test
    void shouldNormalizeToLowercase() {
        Email email = new Email("STUDENT@MAIL.ESCUELAING.EDU.CO");
        assertEquals("student@mail.escuelaing.edu.co", email.getValue());
    }

    @Test
    void shouldThrowExceptionForInvalidDomain() {
        assertThrows(InvalidEmailDomainException.class, () -> new Email("student@gmail.com"));
    }

    @Test
    void shouldThrowExceptionForNullEmail() {
        assertThrows(InvalidEmailDomainException.class, () -> new Email(null));
    }

    @Test
    void shouldThrowExceptionForEmptyEmail() {
        assertThrows(InvalidEmailDomainException.class, () -> new Email(""));
    }

    @Test
    void shouldBeEqualToEmailWithSameValue() {
        Email email1 = new Email("student@mail.escuelaing.edu.co");
        Email email2 = new Email("student@mail.escuelaing.edu.co");
        assertEquals(email1, email2);
    }

    @Test
    void shouldNotBeEqualToEmailWithDifferentValue() {
        Email email1 = new Email("student1@mail.escuelaing.edu.co");
        Email email2 = new Email("student2@mail.escuelaing.edu.co");
        assertNotEquals(email1, email2);
    }

    @Test
    void shouldHaveSameHashCodeForEqualEmails() {
        Email email1 = new Email("student@mail.escuelaing.edu.co");
        Email email2 = new Email("student@mail.escuelaing.edu.co");
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void shouldReturnValueFromToString() {
        Email email = new Email("student@mail.escuelaing.edu.co");
        assertEquals("student@mail.escuelaing.edu.co", email.toString());
    }

    @Test
    void shouldNotEqualNull() {
        Email email = new Email("student@mail.escuelaing.edu.co");
        assertNotEquals(null, email);
    }

    @Test
    void shouldEqualSelf() {
        Email email = new Email("student@mail.escuelaing.edu.co");
        assertEquals(email, email);
    }

    @Test
    void shouldNotEqualDifferentType() {
        Email email = new Email("student@mail.escuelaing.edu.co");
        assertNotEquals(email, "student@mail.escuelaing.edu.co");
    }
}
