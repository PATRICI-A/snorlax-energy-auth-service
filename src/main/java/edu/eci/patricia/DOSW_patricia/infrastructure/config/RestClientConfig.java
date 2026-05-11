package edu.eci.patricia.DOSW_patricia.infrastructure.config;

import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.feign.UserServiceClient;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Bean
    public UserServiceClient userServiceClient(@Value("${user.service.url}") String userServiceUrl) {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(UserServiceClient.class, userServiceUrl);
    }
}
