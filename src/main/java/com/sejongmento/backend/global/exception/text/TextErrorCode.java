package com.sejongmento.backend.global.exception.text;

import com.sejongmento.backend.global.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TextErrorCode implements ErrorCode {
    BAD_WORD_DETECTED(HttpStatus.BAD_REQUEST, "부적절한 단어가 포함되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
