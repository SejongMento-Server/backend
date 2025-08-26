package com.sejongmento.backend.domain.auth.presentation;

import com.sejongmento.backend.domain.auth.application.login.LoginService;
import com.sejongmento.backend.domain.auth.presentation.dto.login.request.LoginRequestDTO;
import com.sejongmento.backend.domain.auth.presentation.dto.login.response.LoginResponseDTO;
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
@RequestMapping("/api/v1/login")
public class LoginController {
    private final LoginService loginService;
    @PostMapping
    public ResponseEntity<CommonResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        var loginInfo = loginService.login(loginRequestDTO);
        return ResponseEntity.ok(CommonResponse.createSuccess(loginInfo));
    }
}

