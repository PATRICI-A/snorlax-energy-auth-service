package edu.eci.patricia.DOSW_patricia.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    private String email;
    private String password;
    private String nombre;
    private String lastName;
    private String programa;
    private Integer semestre;
}
