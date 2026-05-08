package edu.eci.patricia.DOSW_patricia.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

    private String userId;
    private String currentPassword;
    private String newPassword;
}
