package com.sejongmento.backend.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejongmento.backend.global.exception.common.CommonErrorCode;
import com.sejongmento.backend.global.exception.common.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException, ServletException {

        log.info("[AccessDenied] {} | {} | {}", ex.getMessage(), request.getMethod(), request.getRequestURL());

        var ec = CommonErrorCode.FORBIDDEN;
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
