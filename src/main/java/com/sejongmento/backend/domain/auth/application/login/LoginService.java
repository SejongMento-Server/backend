package com.sejongmento.backend.domain.auth.application.login;

import com.sejongmento.backend.domain.auth.presentation.dto.login.request.LoginRequestDTO;
import com.sejongmento.backend.domain.auth.presentation.dto.login.response.LoginResponseDTO;

public interface LoginService {
    LoginResponseDTO login(final LoginRequestDTO loginRequestDTO);
}
