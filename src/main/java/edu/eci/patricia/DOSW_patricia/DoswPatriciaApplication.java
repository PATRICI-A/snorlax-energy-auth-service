package edu.eci.patricia.DOSW_patricia;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "PATRICIA Auth Service", version = "v1"))
@SpringBootApplication
public class DOSWPatriciaApplication {


    public static void main(String[] args) {
        SpringApplication.run(DOSWPatriciaApplication.class, args);
    }
}
