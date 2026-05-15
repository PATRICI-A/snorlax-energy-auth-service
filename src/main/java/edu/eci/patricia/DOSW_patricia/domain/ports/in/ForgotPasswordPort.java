package edu.eci.patricia.DOSW_patricia.domain.ports.in;

/**
 * Input port for initiating the password recovery flow.
 */
public interface ForgotPasswordPort {

    /**
     * Sends a 6-digit recovery code to the user's email.
     *
     * @param email the institutional email of the account to recover
     */
    void forgotPassword(String email);
}
