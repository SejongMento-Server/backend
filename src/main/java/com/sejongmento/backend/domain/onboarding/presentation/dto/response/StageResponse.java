package com.sejongmento.backend.domain.onboarding.presentation.dto.response;

import com.sejongmento.backend.domain.onboarding.domain.enums.OnboardingStep;
import com.sejongmento.backend.domain.user.domain.entity.User;
import com.sejongmento.backend.domain.user.domain.enums.MemberStage;

public record StageResponse(
        MemberStage currentStage,
        OnboardingStep nextStep,
        String regToken // 필요 시 반환 (없으면 null)
) {
    public static StageResponse of(User u, OnboardingStep next, String regToken) {
        return new StageResponse(u.getStage(), next, regToken);
    }
}