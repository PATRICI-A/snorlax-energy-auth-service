package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.RegisterRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.UserAlreadyExistsException;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.RegisterUserPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.EmailSenderPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.Email;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.RolEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase implements RegisterUserPort {

    private final UserRepositoryPort userRepository;
    private final EmailSenderPort emailSender;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequestDto request) {
        Email email = new Email(request.getEmail());

        if (userRepository.existsByEmail(email.getValue())) {
            throw new UserAlreadyExistsException(
                    "A user with email " + email.getValue() + " already exists");
        }

        String code = generateOtpCode();
        OtpEmbedded otp = new OtpEmbedded(code, LocalDateTime.now().plusMinutes(10));

        String hashed = passwordEncoder.encode(request.getPassword());
        User user = new User(
                UUID.randomUUID(),
                email,
                hashed,
                request.getName(),
                request.getLastName(),
                request.getProgram(),
                request.getSemester(),
                request.getInterests(),
                request.getBio(),
                request.getBirthDate(),
                request.getGender(),
                request.getProfileVisibility(),
                RolEnum.STUDENT,
                false,
                0,
                null,
                otp
        );
        userRepository.save(user);

        emailSender.sendOtp(email.getValue(), code);
    }

    private String generateOtpCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
}
