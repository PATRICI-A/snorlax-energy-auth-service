package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Genero;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Interes;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.ProfileVisibility;
import jakarta.validation.constraints.AssertTrue;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Program is required")
    private String program;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    private Integer semester;

    @NotNull(message = "Interests are required")
    @Size(min = 3, message = "You must select at least 3 interests")
    private List<Interes> interests;

    private String bio;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    private Genero gender;

    private ProfileVisibility profileVisibility;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatch() {
        return confirmPassword != null && confirmPassword.equals(password);
    }
}
