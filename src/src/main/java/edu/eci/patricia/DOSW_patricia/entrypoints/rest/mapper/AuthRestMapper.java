package edu.eci.patricia.DOSW_patricia.entrypoints.rest.mapper;

import edu.eci.patricia.DOSW_patricia.application.dto.request.LoginRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.RegisterRequestDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.LoginRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.RegisterRequest;
import edu.eci.patricia.DOSW_patricia.entrypoints.rest.request.ValidateOtpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "programa", source = "programa")
    @Mapping(target = "semestre", source = "semestre")
    RegisterRequestDto toRegisterDto(RegisterRequest request);

    ValidateOtpRequestDto toValidateOtpDto(ValidateOtpRequest request);

    LoginRequestDto toLoginDto(LoginRequest request);
}
