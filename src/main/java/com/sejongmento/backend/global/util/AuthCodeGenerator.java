package com.sejongmento.backend.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AuthCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();
    private final int defaultTokenBytes;

    public AuthCodeGenerator(
            @Value("${app.auth.token-bytes:32}") int defaultTokenBytes) {
        this.defaultTokenBytes = defaultTokenBytes;
    }

    /** (레거시 호환) 6자리 정수 코드. 선행 0 유실 위험 → 가급적 {@link #generate6Digits()} 사용 권장 */
    public int generate() {
        return secureRandom.nextInt(1_000_000);
    }

    /** 6자리 코드(문자열, 000123 형태 보존) */
    public String generate6Digits() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    /** 기본 길이 토큰(Base64 URL-safe, no padding) */
    public String generateToken() {
        return generateToken(defaultTokenBytes);
    }

    /** 지정 길이 토큰(Base64 URL-safe, no padding) */
    public String generateToken(int bytes) {
        byte[] b = new byte[bytes];
        secureRandom.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}