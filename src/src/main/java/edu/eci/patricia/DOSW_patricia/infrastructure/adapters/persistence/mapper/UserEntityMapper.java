package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Email;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity.OtpDocument;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "id", expression = "java(user.getId() != null ? user.getId().toString() : null)")
    @Mapping(target = "otp", expression = "java(toOtpDocument(user.getOtp()))")
    UserEntity toEntity(User user);

    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }

    default OtpDocument toOtpDocument(OtpEmbedded otp) {
        if (otp == null) return null;
        return OtpDocument.builder()
                .codigo(otp.getCodigo())
                .expiraEn(otp.getExpiraEn())
                .usado(otp.getUsado())
                .intentos(otp.getIntentos())
                .build();
    }

    default User toDomain(UserEntity entity) {
        if (entity == null) return null;

        OtpEmbedded otpEmbedded = null;
        if (entity.getOtp() != null) {
            otpEmbedded = new OtpEmbedded(
                    entity.getOtp().getCodigo(),
                    entity.getOtp().getExpiraEn()
            );
            otpEmbedded.setUsado(entity.getOtp().getUsado());
            otpEmbedded.setIntentos(entity.getOtp().getIntentos());
        }

        return new User(
                entity.getId() != null ? UUID.fromString(entity.getId()) : null,
                new Email(entity.getEmail()),
                entity.getHashedPassword(),
                entity.getName(),
                entity.getLastName(),
                entity.getPrograma(),
                entity.getSemestre(),
                entity.getRol(),
                entity.isVerified(),
                entity.getFailedAttempts(),
                entity.getBlockedUntil(),
                otpEmbedded
        );
    }
}
