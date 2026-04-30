package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpDocument {
    private String codigo;
    private LocalDateTime expiraEn;
    private Boolean usado;
    private Integer intentos;
}
