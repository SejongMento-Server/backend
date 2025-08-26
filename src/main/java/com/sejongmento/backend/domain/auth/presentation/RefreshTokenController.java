package com.sejongmento.backend.domain.auth.presentation;


import com.sejongmento.backend.domain.auth.application.token.RefreshTokenService;
import com.sejongmento.backend.domain.auth.presentation.dto.token.request.RefreshTokenRequestDTO;
import com.sejongmento.backend.domain.auth.presentation.dto.token.response.RefreshTokenResponseDTO;
import com.sejongmento.backend.global.common.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<RefreshTokenResponseDTO>> tokenRefresh(
            @RequestBody @Valid RefreshTokenRequestDTO dto
    ) {
        var result = refreshTokenService.refreshToken(dto.getRefreshToken());
        return ResponseEntity.ok(CommonResponse.createSuccess(result));
    }
}
