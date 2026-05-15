package edu.eci.patricia.DOSW_patricia.domain.ports.in;

/**
 * Input port for resending a new OTP to a user.
 */
public interface ResendOtpPort {

    /**
     * Generates and sends a new OTP to the given email address.
     *
     * @param email the institutional email of the user
     */
    void resendOtp(String email);
}
