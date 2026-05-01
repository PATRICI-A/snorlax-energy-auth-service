package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResetPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase implements ResetPasswordPort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void resetPassword(ResetPasswordRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new OtpInvalidException("No account found with that email"));

        OtpEmbedded resetOtp = user.getPasswordResetOtp();
        if (resetOtp == null) {
            throw new OtpInvalidException("No recovery code requested for this account");
        }

        if (resetOtp.haExpirado()) {
            throw new OtpExpiredException("Recovery code has expired. Please request a new one");
        }

        if (Boolean.TRUE.equals(resetOtp.getUsado()) || !resetOtp.getCodigo().equals(dto.getCode())) {
            throw new OtpInvalidException("Invalid recovery code");
        }

        resetOtp.marcaUsado();
        user.setHashedPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
