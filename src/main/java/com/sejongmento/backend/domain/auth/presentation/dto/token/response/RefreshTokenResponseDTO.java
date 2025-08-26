package com.sejongmento.backend.domain.auth.presentation.dto.token.response;

import lombok.Builder;

@Builder
public record RefreshTokenResponseDTO(String accessToken, String refreshToken) {}
