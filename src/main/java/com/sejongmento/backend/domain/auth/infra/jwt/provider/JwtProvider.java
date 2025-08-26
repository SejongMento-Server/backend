package com.sejongmento.backend.domain.auth.infra.jwt.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    /** 액세스 토큰 유효시간: 30분 */
    @Value("${jwt.access-ttl}")
    private long accessMs;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        // secret 이 Base64면 decode, 아니면 그대로 사용
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ignore) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        // 최소 256bit(32바이트) 이상이어야 함
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /** jti(id)에 넣은 userPk 반환 */
    public String getUserIdFromToken(final String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    /** 커스텀 클레임: tokenVersion(tv) */
    public Long getTokenVersionFromToken(final String token) {
        return getClaimFromToken(token, c -> {
            Number n = c.get("tv", Number.class);
            return n != null ? n.longValue() : null;
        });
    }

    public Date getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        if (!validateToken(token)) return null;
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(final String token) {
        // JJWT 0.12.x 파서
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** AccessToken 발급 (tv 포함) */
    public String generateAccessToken(final long id, final long tokenVersion) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tv", tokenVersion);
        return doGenerateAccessToken(String.valueOf(id), claims);
    }

    /** AccessToken 발급 (tv 미포함 버전) */
    public String generateAccessToken(final long id) {
        return doGenerateAccessToken(String.valueOf(id), new HashMap<>());
    }

    /** RefreshToken 발급 */
    public String generateRefreshToken(final long id) {
        return doGenerateRefreshToken(String.valueOf(id));
    }

    private String doGenerateAccessToken(final String id, final Map<String, Object> claims) {
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .id(id) // setId -> id
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessMs))
                .signWith(key) // 알고리즘은 key로부터 유추
                .compact();
    }

    private String doGenerateRefreshToken(final String id) {
        Date now = new Date();
        long refreshMs = (accessMs * 2) * 24; // 기존 계산 그대로(약 24시간)
        return Jwts.builder()
                .id(id)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshMs))
                .signWith(key)
                .compact();
    }

    /** 토큰 유효성 검사 */
    public boolean validateToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT is empty/illegal: {}", e.getMessage());
        }
        return false;
    }
}
