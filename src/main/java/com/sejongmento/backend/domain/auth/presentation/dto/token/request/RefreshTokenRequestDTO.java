package com.sejongmento.backend.domain.auth.presentation.dto.token.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RefreshTokenRequestDTO {
    @NotNull
    @NotEmpty
    private String refreshToken;
}
