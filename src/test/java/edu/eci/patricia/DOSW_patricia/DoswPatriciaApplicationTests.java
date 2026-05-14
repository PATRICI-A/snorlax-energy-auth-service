package edu.eci.patricia.DOSW_patricia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class DoswPatriciaApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

}
