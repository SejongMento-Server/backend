package com.sejongmento.backend.domain.auth.application.login.impl;

import com.sejongmento.backend.domain.auth.application.login.LoginService;
import com.sejongmento.backend.domain.auth.infra.store.RefreshToken;
import com.sejongmento.backend.domain.auth.infra.jwt.provider.JwtProvider;
import com.sejongmento.backend.domain.auth.presentation.dto.login.request.LoginRequestDTO;
import com.sejongmento.backend.domain.auth.presentation.dto.login.response.LoginResponseDTO;
import com.sejongmento.backend.domain.onboarding.application.OnboardingService;
import com.sejongmento.backend.domain.onboarding.domain.enums.OnboardingStep;
import com.sejongmento.backend.domain.user.domain.enums.MemberStage;
import com.sejongmento.backend.domain.user.infra.jpa.UserRepository;
import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.login.LoginErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final OnboardingService onboardingService;

    @Override
    public LoginResponseDTO login(final LoginRequestDTO req) {
        final String email = req.getUserId().trim().toLowerCase();

        var userOpt = userRepository.findByLoginInfoUserId(email);
        if (userOpt.isEmpty()) {
            // 처음 사용자: 세종 인증부터
            return LoginResponseDTO.builder()
                    .stage(MemberStage.UNVERIFIED.name())
                    .step(OnboardingStep.VERIFY.name())
                    .build();
        }

        var user = userOpt.get();
        var step = OnboardingStep.resolve(user);

        // 온보딩 미완료 → 토큰 미발급, 라우팅 정보만 제공
        if (step != OnboardingStep.END) {
            if (step == OnboardingStep.VERIFY) {
                return LoginResponseDTO.builder()
                        .stage(user.getStage().name())
                        .step(OnboardingStep.VERIFY.name())
                        .build();
            }
            // CREDENTIALS_REQUIRED / PROFILE_REQUIRED
            var regTokenOpt = onboardingService.getActiveToken(user); // 재사용만
            if (regTokenOpt.isPresent()) {
                return LoginResponseDTO.builder()
                        .stage(user.getStage().name())
                        .step(step.name())
                        .regToken(regTokenOpt.get())
                        .build();
            }
            // regToken 만료/부재 → 세종 인증부터 다시
            return LoginResponseDTO.builder()
                    .stage(user.getStage().name())
                    .step(OnboardingStep.VERIFY.name())
                    .build();
        }

        // ACTIVE(END): 비번 검증 후 토큰 발급
        if (user.getLoginInfo() == null ||
                !bCryptPasswordEncoder.matches(req.getPassword(), user.getLoginInfo().getPassword())) {
            throw new ApplicationException(LoginErrorCode.NOT_CORRECT);
        }

        String at = jwtProvider.generateAccessToken(user.getId(), user.getTokenVersion());
        RefreshToken.removeUserRefreshToken(user.getId());
        String rt = jwtProvider.generateRefreshToken(user.getId());
        RefreshToken.putRefreshToken(rt, user.getId());

        return LoginResponseDTO.builder()
                .stage(user.getStage().name())
                .step(OnboardingStep.END.name())
                .accessToken(at)
                .refreshToken(rt)
                .build();
    }
}