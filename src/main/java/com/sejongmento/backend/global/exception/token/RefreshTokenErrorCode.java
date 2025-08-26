package com.sejongmento.backend.global.exception.token;

import com.sejongmento.backend.global.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RefreshTokenErrorCode implements ErrorCode {
    NOT_EXIST(HttpStatus.UNAUTHORIZED, "Refresh Token이 존재하지 않습니다."),
    INVALID(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었거나 정상적인 Token이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
