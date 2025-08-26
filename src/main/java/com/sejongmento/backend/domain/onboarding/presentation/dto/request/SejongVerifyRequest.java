package com.sejongmento.backend.domain.onboarding.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SejongVerifyRequest(@NotBlank String portalId,
                                  @NotBlank String portalPw) {}