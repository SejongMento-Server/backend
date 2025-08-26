package com.sejongmento.backend.domain.onboarding.infra.sejong;

import java.util.Map;
public record AuthResponse(boolean success, Boolean isAuth, Integer statusCode, String code, Map<String,Object> body, String authenticator) {}
