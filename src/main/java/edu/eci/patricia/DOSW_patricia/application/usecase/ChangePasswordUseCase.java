package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ChangePasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.InvalidCredentialsException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.TokenInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ChangePasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase implements ChangePasswordPort {

    private final UserServicePort userServicePort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(ChangePasswordRequestDto dto) {
        UserDto user = userServicePort.findById(dto.getUserId())
                .orElseThrow(() -> new TokenInvalidException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.hashedPassword())) {
            throw new InvalidCredentialsException();
        }

        String newHashedPassword = passwordEncoder.encode(dto.getNewPassword());
        userServicePort.updatePassword(user.id(), newHashedPassword);
    }
}
