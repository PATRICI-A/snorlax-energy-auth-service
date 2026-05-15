package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Genero;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Interes;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.ProfileVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * REST request body for the registration endpoint.
 * Aggregates all profile fields required to create a new institutional account.
 * The raw password is hashed before leaving this service; it is never stored in plain text.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Schema(example = "juan.perez@mail.escuelaing.edu.co",
            description = "Institutional email. Must end in @mail.escuelaing.edu.co")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(example = "MiClave123!",
            description = "Raw password (min 8 chars). Must be sent over HTTPS. Stored as BCrypt hash — never persisted in plain text.")
    private String password;

    @NotBlank(message = "Name is required")
    @Schema(example = "Juan")
    private String name;

    @NotBlank(message = "Last name is required")
    @Schema(example = "Pérez")
    private String lastName;

    @NotBlank(message = "Program is required")
    @Schema(example = "Ingeniería de Sistemas")
    private String program;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    @Schema(example = "4", description = "Current academic semester (1–10)")
    private Integer semester;

    @NotNull(message = "Interests are required")
    @Size(min = 3, message = "You must select at least 3 interests")
    @Schema(example = "[\"MUSIC\", \"PROGRAMMING\", \"SPORTS\"]",
            description = "At least 3 interests from the available enum values")
    private List<Interes> interests;

    @Schema(example = "Estudiante apasionado por la tecnología y el fútbol.")
    private String bio;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Schema(example = "2001-08-15")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    @Schema(example = "MALE")
    private Genero gender;

    @NotNull(message = "Profile visibility is required")
    @Schema(example = "PUBLIC",
            description = "PUBLIC: visible to all users. PRIVATE: visible only to yourself. MATCH_ONLY: visible only to affinity matches — hidden from search and feed.")
    private ProfileVisibility profileVisibility;
}
