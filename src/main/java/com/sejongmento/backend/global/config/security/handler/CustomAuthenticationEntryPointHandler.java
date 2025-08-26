package com.sejongmento.backend.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejongmento.backend.global.exception.common.CommonErrorCode;
import com.sejongmento.backend.global.exception.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {

        log.info("[AuthEntryPoint] {} | {} | {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

        var ec = CommonErrorCode.UNAUTHORIZED;
        var body = ErrorResponse.builder()
                .code(ec.name())
                .message(ec.getMessage())
                .build();

        response.setStatus(ec.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }
}