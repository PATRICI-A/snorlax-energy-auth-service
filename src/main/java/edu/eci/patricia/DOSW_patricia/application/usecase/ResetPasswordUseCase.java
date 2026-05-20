package edu.eci.patricia.DOSW_patricia.application.usecase;

import edu.eci.patricia.DOSW_patricia.application.dto.external.UserDto;
import edu.eci.patricia.DOSW_patricia.application.dto.request.ResetPasswordRequestDto;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpExpiredException;
import edu.eci.patricia.DOSW_patricia.domain.exceptions.OtpInvalidException;
import edu.eci.patricia.DOSW_patricia.domain.ports.in.ResetPasswordPort;
import edu.eci.patricia.DOSW_patricia.domain.ports.out.UserServicePort;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.entity.PasswordResetOtpCache;
import edu.eci.patricia.DOSW_patricia.infrastructure.adapters.cache.repository.PasswordResetOtpRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use case for resetting a user's password using a recovery code sent by email.
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase implements ResetPasswordPort {

    private final UserServicePort userServicePort;
    private final PasswordResetOtpRedisRepository passwordResetOtpRedisRepository;

    /**
     * Validates the recovery code and updates the user's password.
     *
     * @param dto contains email, recovery code, and new password
     * @throws OtpInvalidException   if the recovery code is invalid or already used
     * @throws OtpExpiredException   if the recovery code has expired
     */
    @Override
    public void resetPassword(ResetPasswordRequestDto dto) {
        String email = dto.getEmail().trim().toLowerCase();

        UserDto user = userServicePort.findByEmail(email)
                .orElseThrow(() -> new OtpInvalidException("No account found with that email"));

        PasswordResetOtpCache resetOtp = passwordResetOtpRedisRepository.findById(email)
                .orElseThrow(() -> new OtpExpiredException("Recovery code has expired. Please request a new one"));

        if (resetOtp.isUsed() || !resetOtp.getCode().equals(dto.getCode())) {
            throw new OtpInvalidException("Invalid recovery code");
        }

        resetOtp.setUsed(true);
        passwordResetOtpRedisRepository.save(resetOtp);

        userServicePort.updatePassword(user.id().toString(), dto.getNewPassword());
    }
}
