package com.sejongmento.backend.domain.auth.presentation.dto.login.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginRequestDTO {
    @NotNull
    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
