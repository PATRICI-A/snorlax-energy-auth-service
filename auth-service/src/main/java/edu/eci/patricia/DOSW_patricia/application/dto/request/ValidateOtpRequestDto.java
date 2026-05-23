package edu.eci.patricia.DOSW_patricia.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateOtpRequestDto {

    private String email;
    private String otp;
}
