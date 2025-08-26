package com.sejongmento.backend.global.exception.onboarding;

import com.sejongmento.backend.global.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OnboardingErrorCode implements ErrorCode {
    ONBOARDING_INCOMPLETE(HttpStatus.OK, "온보딩이 완료되지 않았습니다. 온보딩을 완료해주세요."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 등록 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "등록 토큰이 만료되었습니다."),
    TOKEN_ALREADY_USED(HttpStatus.CONFLICT, "등록 토큰이 이미 사용되었습니다."),
    REVERIFY_REQUIRED(HttpStatus.UNAUTHORIZED, "검증 유효 시간이 만료되어 재인증이 필요합니다.");


    private final HttpStatus httpStatus;
    private final String message;
}