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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    private String email;
    private String password;
    private String name;
    private String lastName;
    private String program;
    private Integer semester;
    private List<Interes> interests;
    private String bio;
    private LocalDate birthDate;
    private Genero gender;
    private ProfileVisibility profileVisibility;
}
