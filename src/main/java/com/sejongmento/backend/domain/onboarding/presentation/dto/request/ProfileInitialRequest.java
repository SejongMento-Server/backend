package com.sejongmento.backend.domain.onboarding.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProfileInitialRequest(@NotBlank String regToken,
                                    @NotBlank String nickname,
                                    @NotBlank String major,
                                    @NotBlank String desiredJob) {
}
