package com.sejongmento.backend.domain.auth.application.token;

import com.sejongmento.backend.domain.auth.presentation.dto.token.response.RefreshTokenResponseDTO;

public interface RefreshTokenService {
    RefreshTokenResponseDTO refreshToken(final String refreshToken);
}
