package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Genero;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.ProfileVisibility;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private String program;
    private Integer semester;
    private List<String> interests;
    private String bio;
    private LocalDate birthDate;
    private Genero gender;
    private ProfileVisibility profileVisibility;
    private RolEnum rol;
    private boolean verified;
    private Integer failedAttempts;
    private LocalDateTime blockedUntil;
    private OtpDocument otp;
    private OtpDocument passwordResetOtp;
}
