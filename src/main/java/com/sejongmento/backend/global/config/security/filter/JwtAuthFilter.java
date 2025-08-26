package com.sejongmento.backend.global.config.security.filter;

import com.sejongmento.backend.domain.user.application.UserGetService;
import com.sejongmento.backend.domain.auth.infra.jwt.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserGetService userGetService;
    private final JwtAuthenticationFactory authFactory;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String jwt = resolveBearer(req);
            if (jwt != null) {
                Long uid = extractUserId(jwt);
                Long tv  = jwtProvider.getTokenVersionFromToken(jwt);
                if (uid != null) {
                    var snap = userGetService.getAuthById(uid); // id, tokenVersion, stage 만
                    if ("ACTIVE".equalsIgnoreCase(snap.stage()) && tv != null && tv == snap.tokenVersion()) {
                        SecurityContextHolder.getContext().setAuthentication(authFactory.from(snap));
                    }
                }
            }
        }
        chain.doFilter(req, res);
    }

    private String resolveBearer(HttpServletRequest req) {
        String h = req.getHeader("Authorization");
        return (h != null && h.startsWith("Bearer ")) ? h.substring(7) : null;
    }

    private Long extractUserId(String jwt) {
        try {
            String id = jwtProvider.getUserIdFromToken(jwt); // JTI에 userId
            return (id != null && !id.isBlank()) ? Long.parseLong(id) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}