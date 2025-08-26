package com.sejongmento.backend.domain.auth.infra.store;

import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.token.RefreshTokenErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    protected static final Map<String, Long> refreshTokens = new ConcurrentHashMap<>();

    //레디스 캐시

    /**
     * refresh token get
     *
     * @param refreshToken refresh token
     * @return id
     */
    public static Long getRefreshToken(final String refreshToken) {
        return Optional.ofNullable(refreshTokens.get(refreshToken))
                .orElseThrow(() -> new ApplicationException(RefreshTokenErrorCode.NOT_EXIST));
    }

    /**
     * refresh token put
     *
     * @param refreshToken refresh token
     * @param id id
     */
    public static void putRefreshToken(final String refreshToken, Long id) {
        refreshTokens.put(refreshToken, id);
    }

    /**
     * refresh token remove
     *
     * @param refreshToken refresh token
     */
    private static void removeRefreshToken(final String refreshToken) {

        refreshTokens.remove(refreshToken);
    }

    // user refresh token remove
    public static void removeUserRefreshToken(final long userId) {
        refreshTokens.entrySet().removeIf(e -> e.getValue() != null && e.getValue() == userId);
    }

}

