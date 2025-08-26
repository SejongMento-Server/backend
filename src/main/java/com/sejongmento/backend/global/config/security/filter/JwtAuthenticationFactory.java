package com.sejongmento.backend.global.config.security.filter;

import com.sejongmento.backend.domain.user.presentation.dto.response.UserAuthDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
//추후 role 필요해지면(admin 페이지) 여기에 userRole쪽 null 말고 다르게 추가만 하면 됨
public class JwtAuthenticationFactory {
    public Authentication from(UserAuthDTO dto) {
        return new UsernamePasswordAuthenticationToken(dto.id(), null, java.util.Collections.emptyList());
    }
}