package com.sejongmento.backend.domain.user.domain.entity.value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Embeddable
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginInfo {
    @Column(name = "user_id", nullable = true, unique = true)
    private String userId;

    @Column(name = "password", nullable = true, length = 255)
    @JsonIgnore
    private String password;
    public void encryptPassword(PasswordEncoder encoder) {
        if(password == null || password.isEmpty()) throw new IllegalArgumentException("empty password!");
        this.password = encoder.encode(this.password);
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword){
        return passwordEncoder.matches(checkPassword, getPassword());
    }

}