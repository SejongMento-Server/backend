package com.sejongmento.backend.domain.auth.infra.password;

import com.sejongmento.backend.domain.auth.domain.password.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {
    Optional<PasswordResetRequest> findTopByEmailAndCodeUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(String email, LocalDateTime now);
    Optional<PasswordResetRequest> findByResetTokenAndResetTokenExpiresAtAfterAndCompletedFalse(String token, LocalDateTime now);
}
