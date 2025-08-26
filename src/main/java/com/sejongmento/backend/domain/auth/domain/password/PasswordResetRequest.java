package com.sejongmento.backend.domain.auth.domain.password;

import com.sejongmento.backend.domain.user.domain.entity.User;
import com.sejongmento.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_request",
        indexes = {@Index(name="idx_prr_email", columnList="email"), @Index(name="idx_prr_token", columnList="resetToken", unique = true)})
public class PasswordResetRequest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String codeHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;


    @Column(nullable = false)
    private boolean codeUsed;

    @Column(length = 200, unique = true)
    private String resetToken;

    private LocalDateTime resetTokenExpiresAt;

    private boolean completed;

}

