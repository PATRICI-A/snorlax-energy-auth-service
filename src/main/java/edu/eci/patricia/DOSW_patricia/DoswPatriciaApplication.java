package edu.eci.patricia.DOSW_patricia;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the PATRICIA Authentication Service.
 * Bootstraps the Spring Boot application, enables OpenFeign clients, and registers
 * the global OpenAPI/Swagger definition with Bearer JWT security scheme.
 */
@OpenAPIDefinition(info = @Info(title = "PATRICIA Auth Service", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@EnableFeignClients
@SpringBootApplication
public class DoswPatriciaApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(DoswPatriciaApplication.class, args);
    }
}
