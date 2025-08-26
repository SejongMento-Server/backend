package com.sejongmento.backend.global.util.validation;

import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.vane.badwordfiltering.BadWordFiltering;
import org.springframework.stereotype.Component;

import static com.sejongmento.backend.global.exception.text.TextErrorCode.BAD_WORD_DETECTED;

@Component
public class ProfanityValidator {
    //리팩토링 아직 미적용

    private final BadWordFiltering filtering = new BadWordFiltering();

    // 특수문자 우회 방지용(필요한 것 추가/수정)
    private static final String[] SYMBOLS = {"_", "-", ".", "|", "~", "`"};

    /** 비속어 포함 여부 판단 */
    public boolean hasBadWord(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        // 1) 소문자·공백 정규화
        String normalized = text
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();

        // 2) 기본 체크
        if (filtering.check(normalized)) {
            return true;
        }

        // 3) 띄어쓰기 우회
        if (filtering.blankCheck(normalized)) {
            return true;
        }

        // 4) 심볼 우회 심화: 모든 비문자 제거 후 체크
        String lettersOnly = normalized.replaceAll("[^\\p{L}]+", "");
        if (!lettersOnly.equals(normalized) && filtering.check(lettersOnly)) {
            return true;
        }

        // 5) 기존 SYMBOLS 기반 change 검사
        String changed = filtering.change(normalized, SYMBOLS);
        if (!normalized.equals(changed)) {
            return true;
        }

        return false;
    }

    /** 예외 던지기 */
    public void assertNoBadWordOrThrow(String text) {
        if (hasBadWord(text)) {
            throw new ApplicationException(BAD_WORD_DETECTED);
        }
    }
}
