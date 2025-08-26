package com.sejongmento.backend.domain.user.infra.jpa;

import com.sejongmento.backend.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginInfoUserId(String userId);
    Optional<User> findBySejongStudentId(String sejongStudentId);
}
