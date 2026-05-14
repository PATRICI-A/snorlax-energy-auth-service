package edu.eci.patricia.DOSW_patricia.application.dto.request;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Genero;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Interes;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.ProfileVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Application-layer DTO for the user-registration use case.
 * Aggregates all profile fields required to create a new institutional account.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    /** Institutional email (must end in {@code @mail.escuelaing.edu.co}). */
    private String email;
    /** Raw password — hashed with BCrypt before persistence. */
    private String password;
    /** User's first name. */
    private String name;
    /** User's last name. */
    private String lastName;
    /** Academic programme (e.g. "Ingeniería de Sistemas"). */
    private String program;
    /** Current academic semester (1–10). */
    private Integer semester;
    /** List of at least 3 interests selected by the user. */
    private List<Interes> interests;
    /** Optional short biography. */
    private String bio;
    /** Date of birth — must be in the past. */
    private LocalDate birthDate;
    /** User's gender identity. */
    private Genero gender;
    /** Controls who can view the user's profile. */
    private ProfileVisibility profileVisibility;
}
