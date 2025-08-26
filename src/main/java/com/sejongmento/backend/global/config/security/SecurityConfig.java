package com.sejongmento.backend.global.config.security;

import com.sejongmento.backend.global.config.security.filter.JwtAuthFilter;
import com.sejongmento.backend.global.config.security.handler.CustomAccessDeniedHandler;
import com.sejongmento.backend.global.config.security.handler.CustomAuthenticationEntryPointHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPointHandler authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        // SecurityUrls.AUTH_WHITELIST -> MvcRequestMatcher[] 로 변환
        MvcRequestMatcher[] permitAll = SecurityUrls.AUTH_WHITELIST.stream()
                .map(mvc::pattern)
                .toArray(MvcRequestMatcher[]::new);

        http
                // CORS
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                // CSRF/Form/Logout 비활성
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // 세션 미사용
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitAll).permitAll()
                        // 프리플라이트(OPTIONS) 허용
                        .requestMatchers(mvc.pattern(HttpMethod.OPTIONS, "/**")).permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 필터
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // 인증/인가 예외 핸들러
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        // 로컬 프론트 도메인 허용 (패턴 기반)
        conf.setAllowedOriginPatterns(SecurityUrls.ALLOWED_ORIGINS);
        conf.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        conf.setAllowedHeaders(List.of("*"));
        conf.setAllowCredentials(true);
        conf.setMaxAge(3600L);
        // (필요 시) 노출 헤더 추가
        // conf.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}