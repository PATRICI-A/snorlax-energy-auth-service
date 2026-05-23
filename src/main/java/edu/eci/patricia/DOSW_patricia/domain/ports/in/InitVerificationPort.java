package edu.eci.patricia.DOSW_patricia.domain.ports.in;


/**
 * Input port for starting the OTP verification flow after user registration.
 */
public interface InitVerificationPort {

    /**
     * Generates and sends an OTP to the email in the given request.
     *
     * @param dto contains the institutional email and hashed password of the new user
     */
    void initVerification(String mail);
}
