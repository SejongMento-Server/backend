package com.sejongmento.backend.domain.onboarding.infra.jpa;

import com.sejongmento.backend.domain.onboarding.domain.entity.RegistrationToken;
import com.sejongmento.backend.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {
    Optional<RegistrationToken> findByToken(String token);
    Optional<RegistrationToken> findTop1ByUserAndUsedFalseAndExpiresAtAfterOrderByExpiresAtDesc(
            User user, LocalDateTime now);
}
