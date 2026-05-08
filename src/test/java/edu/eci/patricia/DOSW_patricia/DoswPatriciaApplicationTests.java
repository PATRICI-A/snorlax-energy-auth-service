package edu.eci.patricia.DOSW_patricia;

import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper.AuthRestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = "JWT_SECRET=test-secret-key-for-context-loading-min32chars!!")
class DoswPatriciaApplicationTests {

    @MockitoBean
    JavaMailSender javaMailSender;

    @MockitoBean
    UserServicePort userServicePort;

    // MapStruct 1.6.3 + Spring Boot 4: AuthRestMapperImpl generated bean is not
    // picked up by the test context component scan. Mock it explicitly here.
    @MockitoBean
    AuthRestMapper authRestMapper;

    @Test
    void contextLoads() {
    }

}
