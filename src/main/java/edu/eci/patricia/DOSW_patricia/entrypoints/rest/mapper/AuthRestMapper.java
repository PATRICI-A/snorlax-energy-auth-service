package edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper;

import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.RegisterRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.LoginRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.RegisterRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ResetPasswordRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ValidateOtpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "program", source = "program")
    @Mapping(target = "semester", source = "semester")
    @Mapping(target = "interests", source = "interests")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "profileVisibility", source = "profileVisibility")
    RegisterRequestDto toRegisterDto(RegisterRequest request);

    ValidateOtpRequestDto toValidateOtpDto(ValidateOtpRequest request);

    LoginRequestDto toLoginDto(LoginRequest request);

    @Mapping(target = "code", source = "code")
    @Mapping(target = "newPassword", source = "newPassword")
    ResetPasswordRequestDto toResetPasswordDto(ResetPasswordRequest request);
}
