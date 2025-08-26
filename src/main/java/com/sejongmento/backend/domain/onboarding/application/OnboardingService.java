package com.sejongmento.backend.domain.onboarding.application;

import com.sejongmento.backend.domain.onboarding.domain.entity.RegistrationToken;
import com.sejongmento.backend.domain.onboarding.domain.enums.OnboardingStep;
import com.sejongmento.backend.domain.onboarding.infra.jpa.RegistrationTokenRepository;
import com.sejongmento.backend.domain.onboarding.presentation.dto.request.ProfileInitialRequest;
import com.sejongmento.backend.domain.onboarding.presentation.dto.request.SignupCredentialsRequest;
import com.sejongmento.backend.domain.onboarding.presentation.dto.response.StageResponse;
import com.sejongmento.backend.domain.profile.application.ProfileService;
import com.sejongmento.backend.domain.user.domain.entity.User;
import com.sejongmento.backend.domain.user.domain.enums.MemberStage;
import com.sejongmento.backend.domain.user.infra.jpa.UserRepository;
import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.onboarding.OnboardingErrorCode;
import com.sejongmento.backend.global.util.AuthCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OnboardingService {

    @Value("${app.onboarding.token-ttl-seconds:1800}") // 30분
    private long tokenTtlSeconds;

    private final UserRepository userRepo;
    private final RegistrationTokenRepository regRepo;
    private final PasswordEncoder encoder;
    private final ProfileService profileService;
    private final AuthCodeGenerator codeGen;

    /** 1단계: 세종 인증 성공 → VERIFIED + 등록 토큰 발급 */
    @Transactional
    public StageResponse upsertVerifiedAndMintToken(final String sejongStudentId) {
        var user = userRepo.findBySejongStudentId(sejongStudentId)
                .orElseGet(() -> User.builder().sejongStudentId(sejongStudentId).build());

        if (user.getStage() == MemberStage.ACTIVE) {
            return StageResponse.of(user, OnboardingStep.END, null);
        }

        user = userRepo.save(user);

        final String token = codeGen.generateToken();
        regRepo.save(RegistrationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(tokenTtlSeconds))
                .used(false)
                .build());

        final OnboardingStep next = OnboardingStep.resolve(user);
        return StageResponse.of(user, next, token);
    }

    /** 2단계: 자격(계정) 설정 */
    @Transactional
    public StageResponse setCredentialsAndDescribeNext(final SignupCredentialsRequest req) {
        final RegistrationToken rt = requireActiveToken(req.regToken());
        final User user = rt.getUser();

        user.saveCredentials(req.email().trim().toLowerCase(), req.password(), encoder);

        final OnboardingStep next = OnboardingStep.resolve(user);
        return StageResponse.of(user, next, rt.getToken()); // 2~3단계 동일 토큰 유지
    }

    /** 3단계: 프로필 완료 */
    @Transactional
    public StageResponse completeProfileAndDescribeNext(final ProfileInitialRequest req) {
        final RegistrationToken rt = requireActiveToken(req.regToken());
        final User user = rt.getUser();

        profileService.createBasicFor(user, req.nickname(), req.major(), req.desiredJob());
        user.activate();

        rt.setUsed(true); // 완료

        final OnboardingStep next = OnboardingStep.resolve(user); // END
        return StageResponse.of(user, next, null);
    }

    @Transactional(readOnly = true)
    public Optional<String> getActiveToken(User user) {
        var now = LocalDateTime.now();
        return regRepo
                .findTop1ByUserAndUsedFalseAndExpiresAtAfterOrderByExpiresAtDesc(user, now)
                .map(RegistrationToken::getToken);
    }

    /** 토큰 존재/미사용/미만료 검증 (window 제거 → token TTL만) */
    private RegistrationToken requireActiveToken(final String token) {
        final RegistrationToken rt = regRepo.findByToken(token)
                .orElseThrow(() -> new ApplicationException(OnboardingErrorCode.INVALID_TOKEN));

        if (rt.isUsed()) {
            throw new ApplicationException(OnboardingErrorCode.TOKEN_ALREADY_USED);
        }
        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApplicationException(OnboardingErrorCode.TOKEN_EXPIRED);
        }
        return rt;
    }
}