package com.sejongmento.backend.domain.auth.presentation;

import com.sejongmento.backend.domain.auth.application.password.PasswordResetService;
import com.sejongmento.backend.domain.auth.presentation.dto.password.request.ResetConfirm;
import com.sejongmento.backend.domain.auth.presentation.dto.password.request.ResetRequest;
import com.sejongmento.backend.domain.auth.presentation.dto.password.request.ResetVerify;
import com.sejongmento.backend.domain.auth.presentation.dto.password.response.ResetVerifyResponse;
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
@RequestMapping("/auth/password/reset")
public class PasswordResetController {

    private final PasswordResetService service;

    @PostMapping("/request")
    public ResponseEntity<CommonResponse<Void>> request(@RequestBody @Valid ResetRequest dto) {
        service.requestReset(dto.email());
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent("인증 코드가 이메일로 전송되었습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<ResetVerifyResponse>> verify(@RequestBody @Valid ResetVerify dto) {
        String token = service.verifyCode(dto.email(), dto.code());
        return ResponseEntity.ok(CommonResponse.createSuccess(new ResetVerifyResponse(token)));
    }

    @PostMapping("/confirm")
    public ResponseEntity<CommonResponse<Void>> confirm(@RequestBody @Valid ResetConfirm dto) {
        service.confirmReset(dto.resetToken(), dto.newPassword());
        return ResponseEntity.ok(CommonResponse.createSuccessWithNoContent("비밀번호가 변경되었습니다."));
    }
}
