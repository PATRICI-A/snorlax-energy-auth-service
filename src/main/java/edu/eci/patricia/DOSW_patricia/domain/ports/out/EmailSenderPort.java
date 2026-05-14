package edu.eci.patricia.DOSW_patricia.domain.ports.out;

import java.util.UUID;

/**
 * Output port for sending emails via an external messaging system (RabbitMQ).
 */
public interface EmailSenderPort {

    /**
     * Sends an OTP verification code to the given email address.
     *
     * @param to      recipient email address
     * @param otpCode 6-digit OTP code to send
     */
    void sendOtp(String to, String otpCode);

    /**
     * Sends a password recovery code to the given email address.
     *
     * @param to     recipient email address
     * @param code   6-digit recovery code
     * @param userId ID of the user requesting the password reset
     */
    void sendPasswordReset(String to, String code, UUID userId);
}
