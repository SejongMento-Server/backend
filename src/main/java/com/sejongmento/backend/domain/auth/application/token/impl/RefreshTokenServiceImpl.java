package com.sejongmento.backend.domain.auth.application.token.impl;

import com.sejongmento.backend.domain.auth.application.token.RefreshTokenService;
import com.sejongmento.backend.domain.auth.infra.store.RefreshToken;
import com.sejongmento.backend.domain.auth.infra.jwt.provider.JwtProvider;
import com.sejongmento.backend.domain.auth.presentation.dto.token.response.RefreshTokenResponseDTO;
import com.sejongmento.backend.domain.user.application.UserGetService;
import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.token.RefreshTokenErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProvider jwtProvider;
    private final UserGetService userGetService;

    @Override
    public RefreshTokenResponseDTO refreshToken(final String refreshToken) {
        if(Boolean.FALSE.equals(jwtProvider.validateToken(refreshToken))){
            throw new ApplicationException(RefreshTokenErrorCode.INVALID);
        }

        var id = RefreshToken.getRefreshToken(refreshToken);
        var auth = userGetService.getAuthById(id);

        String newAccess =  jwtProvider.generateAccessToken(id, auth.tokenVersion());
        RefreshToken.removeUserRefreshToken(id);
        String newRefresh = jwtProvider.generateRefreshToken(id);
        RefreshToken.putRefreshToken(newRefresh, id);
        return RefreshTokenResponseDTO.builder().accessToken(newAccess).refreshToken(newRefresh).build();

    }
}
