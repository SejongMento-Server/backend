package com.sejongmento.backend.domain.auth.presentation.dto.login.response;


import lombok.Builder;

@Builder
public record LoginResponseDTO(
        String stage,        // MemberStage (UNVERIFIED/VERIFIED/ACTIVE)
        String step,         // OnboardingStep (END/VERIFY/REVERIFY/CREDENTIALS_REQUIRED/PROFILE_REQUIRED)
        String accessToken,  // step==END 일 때만
        String refreshToken, // step==END 일 때만
        String regToken      // step이 VERIFIED|CREDENTIALS_REQUIRED|PROFILE_REQUIRED 이면 (윈도우 유효 시) 동봉
) {}