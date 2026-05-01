package edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.mapper;

import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Email;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Interes;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity.OtpDocument;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "emailToString")
    @Mapping(target = "id", expression = "java(user.getId() != null ? user.getId().toString() : null)")
    @Mapping(target = "otp", expression = "java(toOtpDocument(user.getOtp()))")
    @Mapping(target = "passwordResetOtp", expression = "java(toOtpDocument(user.getPasswordResetOtp()))")
    @Mapping(target = "interests", expression = "java(interesesToStrings(user.getInterests()))")
    UserEntity toEntity(User user);

    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }

    default List<String> interesesToStrings(List<Interes> interests) {
        if (interests == null) return null;
        return interests.stream().map(Interes::name).collect(Collectors.toList());
    }

    default List<Interes> stringsToIntereses(List<String> interests) {
        if (interests == null) return null;
        return interests.stream().map(Interes::valueOf).collect(Collectors.toList());
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

        OtpEmbedded otpEmbedded = toOtpEmbedded(entity.getOtp());
        OtpEmbedded passwordResetOtpEmbedded = toOtpEmbedded(entity.getPasswordResetOtp());

        User user = new User(
                entity.getId() != null ? UUID.fromString(entity.getId()) : null,
                new Email(entity.getEmail()),
                entity.getHashedPassword(),
                entity.getName(),
                entity.getLastName(),
                entity.getProgram(),
                entity.getSemester(),
                stringsToIntereses(entity.getInterests()),
                entity.getBio(),
                entity.getBirthDate(),
                entity.getGender(),
                entity.getProfileVisibility(),
                entity.getRol(),
                entity.isVerified(),
                entity.getFailedAttempts(),
                entity.getBlockedUntil(),
                otpEmbedded
        );
        user.setPasswordResetOtp(passwordResetOtpEmbedded);
        return user;
    }

    default OtpEmbedded toOtpEmbedded(OtpDocument doc) {
        if (doc == null) return null;
        OtpEmbedded embedded = new OtpEmbedded(doc.getCodigo(), doc.getExpiraEn());
        embedded.setUsado(doc.getUsado());
        embedded.setIntentos(doc.getIntentos());
        return embedded;
    }
}
