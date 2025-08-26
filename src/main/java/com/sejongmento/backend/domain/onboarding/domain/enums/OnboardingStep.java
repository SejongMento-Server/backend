package com.sejongmento.backend.domain.onboarding.domain.enums;

import com.sejongmento.backend.domain.user.domain.entity.User;
import com.sejongmento.backend.domain.user.domain.enums.MemberStage;

public enum OnboardingStep {
    VERIFY,                 // 세종 인증 필요 (1단계)
    CREDENTIALS_REQUIRED,   // 이메일/비번 설정 (2단계)
    PROFILE_REQUIRED,       // 프로필 기본 정보 완료 (3단계)
    END;                    // 더 할 것 없음 (ACTIVE)

    public static OnboardingStep resolve(User u) {
        if (u.getStage() == MemberStage.UNVERIFIED) return VERIFY;
        if (u.getStage() == MemberStage.VERIFIED) {
            if (u.getLoginInfo() == null) return CREDENTIALS_REQUIRED;
            if (u.getProfile() == null)   return PROFILE_REQUIRED;
            return END;
        }
        return END; // ACTIVE
    }
}