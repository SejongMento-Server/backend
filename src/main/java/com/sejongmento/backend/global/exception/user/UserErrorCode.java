package com.sejongmento.backend.global.exception.user;

import com.sejongmento.backend.global.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NOT_EXISTS(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."),
    EMAIL_IMMUTABLE(HttpStatus.CONFLICT, "이미 저장된 이메일은 변경할 수 없습니다."),
    PROFILE_ALREADY_ATTACHED(HttpStatus.CONFLICT, "이미 다른 프로필이 연결되어 있습니다."),
    CREDENTIALS_NOT_SET(HttpStatus.BAD_REQUEST, "사용자 자격증명이 설정되어 있지 않습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
