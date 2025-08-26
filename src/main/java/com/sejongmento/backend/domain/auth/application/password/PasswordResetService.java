package com.sejongmento.backend.domain.auth.application.password;

import com.sejongmento.backend.domain.auth.application.password.email.EmailService;
import com.sejongmento.backend.domain.auth.domain.password.PasswordResetRequest;
import com.sejongmento.backend.domain.auth.infra.password.PasswordResetRequestRepository;
import com.sejongmento.backend.domain.auth.infra.store.RefreshToken;
import com.sejongmento.backend.domain.user.domain.entity.User;
import com.sejongmento.backend.domain.user.infra.jpa.UserRepository;
import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.password.PasswordResetErrorCode;
import com.sejongmento.backend.global.util.AuthCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepo;
    private final PasswordResetRequestRepository prrRepo;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    private final AuthCodeGenerator codeGen;

    @Value("${app.password-reset.code-ttl-seconds:180}")
    private int codeTtlSeconds;

    @Value("${app.password-reset.token-ttl-seconds:600}")
    private int tokenTtlSeconds;

    /** 1) 리셋 코드 요청 */
    @Transactional
    public void requestReset(final String emailRaw) {
        final String email = (emailRaw == null ? "" : emailRaw.trim().toLowerCase(Locale.ROOT));
        if (email.isBlank()) {
            throw new ApplicationException(PasswordResetErrorCode.EMAIL_REQUIRED);
        }
        var userOpt = userRepo.findByLoginInfoUserId(email);
        if (userOpt.isEmpty()) {
            return;
        }
        var user = userOpt.get();
        final String code = codeGen.generate6Digits();
        final String codeHash = encoder.encode(code);

        final PasswordResetRequest req = PasswordResetRequest.builder()
                .email(email)
                .user(user)
                .codeHash(codeHash)
                .expiresAt(LocalDateTime.now().plusSeconds(codeTtlSeconds))
                .codeUsed(false)
                .completed(false)
                .build();

        prrRepo.save(req);

        try {
            emailService.sendPasswordResetCode(email, code, codeTtlSeconds);
        } catch (Exception e) {
            // 필요 시 세부 cause를 detail message로 함께 전달해도 됨
            throw new ApplicationException(PasswordResetErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    /** 2) 코드 검증 → 리셋 토큰 발급 */
    @Transactional
    public String verifyCode(final String emailRaw, final String code) {
        final String email = (emailRaw == null ? "" : emailRaw.trim().toLowerCase(Locale.ROOT));
        if (email.isBlank()) {
            throw new ApplicationException(PasswordResetErrorCode.EMAIL_REQUIRED);
        }

        final PasswordResetRequest req = prrRepo
                .findTopByEmailAndCodeUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(email, LocalDateTime.now())
                .orElseThrow(() -> new ApplicationException(PasswordResetErrorCode.CODE_INVALID_OR_EXPIRED));




        if (!encoder.matches(code, req.getCodeHash())) {
            throw new ApplicationException(PasswordResetErrorCode.CODE_INVALID_OR_EXPIRED);
        }
        req.setCodeUsed(true);
        final String resetToken = codeGen.generateToken();
        req.setResetToken(resetToken);
        req.setResetTokenExpiresAt(LocalDateTime.now().plusSeconds(tokenTtlSeconds));

        return resetToken;
    }

    /** 3) 최종 비밀번호 변경 */
    @Transactional
    public void confirmReset(final String resetToken, final String newPasswordRaw) {
        if (newPasswordRaw == null || newPasswordRaw.isBlank()) {
            throw new ApplicationException(PasswordResetErrorCode.PASSWORD_REQUIRED);
        }

        PasswordResetRequest req = prrRepo
                .findByResetTokenAndResetTokenExpiresAtAfterAndCompletedFalse(resetToken, LocalDateTime.now())
                .orElseThrow(() -> new ApplicationException(PasswordResetErrorCode.RESET_TOKEN_INVALID_OR_EXPIRED));

        User user = req.getUser();
        if (user == null) {
            throw new ApplicationException(PasswordResetErrorCode.ACCOUNT_NOT_FOUND);
        }

        user.changePassword(newPasswordRaw, encoder);

        // 모든 기존 Refresh Token 무효화
        user.increaseTokenVersion();
        RefreshToken.removeUserRefreshToken(user.getId());

        // 요청 완료 플래그
        req.setCompleted(true);

    }


}
