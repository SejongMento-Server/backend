package com.sejongmento.backend.domain.onboarding.infra.sejong;


import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.sejong.SejongAuthErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SejongVerifierImpl implements SejongVerifier {

    private final List<Authenticator> chain = List.of(
            new PortalSSOToken(),
            new ClassicSession(),
            new MoodlerSession(),
            new DosejongSession()
    );

    /**
     * 인증 성공 시 학번/아이디를 반환하고,
     * 실패/장애는 ApplicationException으로 던진다.
     */
    @Override
    public String verifyAndGetStudentId(String portalId, String portalPw) {
        boolean explicitDenied = false;   // 어떤 체인이든 "인증 실패"를 명시했는가
        boolean attempted = false;        // 최소 1개 이상 시도했는가

        for (Authenticator a : chain) {
            try {
                attempted = true;
                AuthResponse res = a.authenticate(portalId, portalPw);
                Boolean auth = res.isAuth(); // tri-state: true/false/null

                if (Boolean.TRUE.equals(auth)) {
                    return portalId; // 성공
                }
                if (Boolean.FALSE.equals(auth)) {
                    explicitDenied = true;   // 명시적 실패
                    break;                   // 더 볼 필요 없음(정책에 따라 continue도 가능)
                }
                // null 이면 판단불가 → 다음 체인으로
            } catch (Exception ignored) {
                // 개별 체인 오류는 삼키고 다음 체인 시도
            }
        }

        if (explicitDenied) {
            throw new ApplicationException(SejongAuthErrorCode.AUTH_FAILED);
        }
        // 모든 체인이 판단불가 또는 예외 → 포털 장애로 간주
        if (attempted) {
            throw new ApplicationException(SejongAuthErrorCode.PORTAL_UNAVAILABLE);
        }
        // 체인 자체가 비어있거나 전혀 시도되지 않은 비정상 케이스
        throw new ApplicationException(SejongAuthErrorCode.PORTAL_UNAVAILABLE);
    }
}
