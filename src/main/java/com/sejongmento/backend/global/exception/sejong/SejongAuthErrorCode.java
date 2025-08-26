package com.sejongmento.backend.global.exception.sejong;

import com.sejongmento.backend.global.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SejongAuthErrorCode implements ErrorCode {
    AUTH_FAILED(HttpStatus.UNAUTHORIZED, "세종 포털 인증 실패"),
    PORTAL_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "세종 포털에 접속할 수 없습니다. 잠시 후 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String message;
}