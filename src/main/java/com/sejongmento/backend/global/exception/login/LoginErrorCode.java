package com.sejongmento.backend.global.exception.login;

import com.sejongmento.backend.global.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LoginErrorCode implements ErrorCode {
    NOT_CORRECT(HttpStatus.OK, "아이디 혹은 비밀번호가 일치하지 않습니다.");
    private final HttpStatus httpStatus;
    private final String message;
}
