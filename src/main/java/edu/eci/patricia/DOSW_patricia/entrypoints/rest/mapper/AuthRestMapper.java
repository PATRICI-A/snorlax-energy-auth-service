package edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper;

import edu.eci.patricia.DOSW_patricia.application.dto.request.InitVerificationRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.InitVerificationRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.LoginRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ResetPasswordRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ValidateOtpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    InitVerificationRequestDto toInitVerificationDto(InitVerificationRequest request);

    ValidateOtpRequestDto toValidateOtpDto(ValidateOtpRequest request);

    LoginRequestDto toLoginDto(LoginRequest request);

    @Mapping(target = "code", source = "code")
    @Mapping(target = "newPassword", source = "newPassword")
    ResetPasswordRequestDto toResetPasswordDto(ResetPasswordRequest request);
}
