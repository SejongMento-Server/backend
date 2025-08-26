package com.sejongmento.backend.domain.auth.application.password.email;

public interface EmailService {
    void sendPasswordResetCode(String toEmail, String code, int ttlSeconds);
}
