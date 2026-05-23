package edu.eci.patricia.DOSW_patricia.domain.ports.out;

public interface EmailSenderPort {

    void sendOtp(String to, String otpCode);

    void sendPasswordReset(String to, String code);
}
