package edu.eci.patricia.DOSW_patricia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DoswPatriciaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoswPatriciaApplication.class, args);
    }
}
