package edu.eci.patricia.DOSW_patricia;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.repository.RefreshTokenMongoRepository;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.repository.UserMongoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.data.mongo.MongoAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration," +
        "org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration"
})
class DoswPatriciaApplicationTests {

    @MockitoBean
    UserMongoRepository userMongoRepository;
    @MockitoBean
    RefreshTokenMongoRepository refreshTokenMongoRepository;
    @MockitoBean
    JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
    }

}
