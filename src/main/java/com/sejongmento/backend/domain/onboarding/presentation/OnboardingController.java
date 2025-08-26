package com.sejongmento.backend.domain.onboarding.presentation;

import com.sejongmento.backend.domain.onboarding.application.OnboardingService;
import com.sejongmento.backend.domain.onboarding.infra.sejong.SejongVerifier;
import com.sejongmento.backend.domain.onboarding.presentation.dto.request.ProfileInitialRequest;
import com.sejongmento.backend.domain.onboarding.presentation.dto.request.SejongVerifyRequest;
import com.sejongmento.backend.domain.onboarding.presentation.dto.request.SignupCredentialsRequest;
import com.sejongmento.backend.domain.onboarding.presentation.dto.response.StageResponse;
import com.sejongmento.backend.global.common.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
public class OnboardingController {

    private final SejongVerifier sejongVerifier;
    private final OnboardingService onboardingService;

    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<?>> verify(@RequestBody @Valid SejongVerifyRequest req) {
        String sid = sejongVerifier.verifyAndGetStudentId(req.portalId(), req.portalPw());
        return ResponseEntity.ok(CommonResponse.createSuccess(onboardingService.upsertVerifiedAndMintToken(sid)));
    }

    @PostMapping("/credentials")
    public ResponseEntity<CommonResponse<StageResponse>> credentials(@RequestBody @Valid SignupCredentialsRequest req) {
        return ResponseEntity.ok(CommonResponse.createSuccess(onboardingService.setCredentialsAndDescribeNext(req)));
    }

    @PostMapping("/profile")
    public ResponseEntity<CommonResponse<StageResponse>> profile(@RequestBody @Valid ProfileInitialRequest req) {
        return ResponseEntity.ok(CommonResponse.createSuccess(onboardingService.completeProfileAndDescribeNext(req)));
    }
}