package edu.eci.patricia.DOSW_patricia.entrypoints.rest.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String confirmarPassword;

    @NotBlank(message = "Name is required")
    private String nombre;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Program is required")
    private String programa;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    private Integer semestre;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatch() {
        return confirmarPassword != null && confirmarPassword.equals(password);
    }
}
