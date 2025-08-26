package com.sejongmento.backend.global.exception.password;

import com.sejongmento.backend.global.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PasswordResetErrorCode implements ErrorCode {
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "이메일은 필수입니다."),
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "새 비밀번호는 필수입니다."),
    EMAIL_NOT_REGISTERED(HttpStatus.NOT_FOUND, "가입된 이메일이 아닙니다."),
    CODE_INVALID_OR_EXPIRED(HttpStatus.BAD_REQUEST, "인증 코드가 유효하지 않거나 만료되었습니다."),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "인증 코드 시도 횟수를 초과했습니다."),
    RESET_TOKEN_INVALID_OR_EXPIRED(HttpStatus.UNAUTHORIZED, "비밀번호 재설정 토큰이 유효하지 않거나 만료되었습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 계정을 찾을 수 없습니다."),
    EMAIL_SENDING_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "인증 코드를 이메일로 전송하지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}