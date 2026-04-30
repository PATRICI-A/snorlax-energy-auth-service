package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String hashedPassword;
    private String name;
    private String lastName;
    private String programa;
    private Integer semestre;
    private RolEnum rol;
    private boolean verified;
    private Integer failedAttempts;
    private LocalDateTime blockedUntil;
    private OtpDocument otp;
}
