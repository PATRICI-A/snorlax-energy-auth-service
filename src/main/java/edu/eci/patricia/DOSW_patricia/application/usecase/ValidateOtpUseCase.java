package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.request.ValidateOtpRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.model.User;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ValidateOtpPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserRepositoryPort;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpCode;
import edu.eci.patricia.DOSW_patricia.domain.valueobjects.OtpEmbedded;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateOtpUseCase implements ValidateOtpPort {

    private final UserRepositoryPort userRepository;

    @Override
    public void validateOtp(ValidateOtpRequestDto request) {
        new OtpCode(request.getOtp());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new OtpInvalidException("No user found for this email"));

        OtpEmbedded otp = user.getOtp();
        if (otp == null) {
            throw new OtpInvalidException("No OTP found for this user");
        }

        if (otp.haExpirado()) {
            throw new OtpExpiredException("OTP has expired. Please request a new one");
        }

        if (Boolean.TRUE.equals(otp.getUsado()) || !otp.getCodigo().equals(request.getOtp())) {
            throw new OtpInvalidException("Invalid OTP");
        }

        otp.marcaUsado();
        user.verify();
        user.resetLockout();
        userRepository.save(user);
    }
}
