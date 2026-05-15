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

/**
 * MapStruct mapper for converting REST request objects into application-layer DTOs.
 */
@Mapper(componentModel = "spring")
public interface AuthRestMapper {

    /**
     * Converts an init-verification REST request to its application DTO.
     *
     * @param request the REST request
     * @return the application DTO
     */
    InitVerificationRequestDto toInitVerificationDto(InitVerificationRequest request);

    /**
     * Converts a validate-OTP REST request to its application DTO.
     *
     * @param request the REST request
     * @return the application DTO
     */
    ValidateOtpRequestDto toValidateOtpDto(ValidateOtpRequest request);

    /**
     * Converts a login REST request to its application DTO.
     *
     * @param request the REST request
     * @return the application DTO
     */
    LoginRequestDto toLoginDto(LoginRequest request);

    /**
     * Converts a reset-password REST request to its application DTO.
     *
     * @param request the REST request
     * @return the application DTO
     */
    @Mapping(target = "code", source = "code")
    @Mapping(target = "newPassword", source = "newPassword")
    ResetPasswordRequestDto toResetPasswordDto(ResetPasswordRequest request);
}
