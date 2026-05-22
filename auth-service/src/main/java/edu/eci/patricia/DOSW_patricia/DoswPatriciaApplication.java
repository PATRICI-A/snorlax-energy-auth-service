package edu.eci.patricia.DOSW_patricia;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service API",
                version = "v1.0.0",
                description = "This microservice handles the complete authentication lifecycle for the PATRICIA " +
                        "university social network. Responsibilities include: new-user OTP-based email " +
                        "verification, credential-based login with brute-force protection (account lockout after " +
                        "5 failed attempts for 30 minutes), JWT access and refresh token issuance and rotation, " +
                        "session revocation (logout), and all password management flows (forgot password, reset " +
                        "password, and authenticated change password). All JWT tokens are signed with " +
                        "HMAC-SHA256. Passwords are hashed with BCrypt and never stored in plain text. " +
                        "OTP codes and refresh tokens are stored in Redis with appropriate TTLs.",
                contact = @Contact(
                        name = "Snorlax Energy — Auth Team",
                        email = "auth-team@snorlax.energy"
                )
        ),
        servers = @Server(url = "http://localhost:8080", description = "Local development server")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "JWT Bearer token. Obtain one from POST /api/v1/auth/login or POST /api/v1/auth/verify-otp. " +
                "Include it in the Authorization header as: Bearer <token>"
)
@SpringBootApplication
public class DoswPatriciaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoswPatriciaApplication.class, args);
    }
}
