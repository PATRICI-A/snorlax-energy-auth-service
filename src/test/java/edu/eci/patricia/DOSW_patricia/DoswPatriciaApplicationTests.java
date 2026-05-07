package edu.eci.patricia.DOSW_patricia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class DoswPatriciaApplicationTests {

    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
    }

}
