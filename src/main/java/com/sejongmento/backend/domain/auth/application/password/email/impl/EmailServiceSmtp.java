package com.sejongmento.backend.domain.auth.application.password.email.impl;

import com.sejongmento.backend.domain.auth.application.password.email.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceSmtp implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${app.mail.from}") private String from;
    @Value("${app.mail.from-name}") private String fromName;

    @Override
    public void sendPasswordResetCode(String toEmail, String code, int ttlSeconds) {
        String subject = "[Sejong App] 비밀번호 재설정 인증코드";
        String text = String.format("비밀번호 재설정 코드: %s%n%d분 내에 입력해 주세요.", code, Math.max(1, ttlSeconds/60));
        String html = String.format("<p>아래 인증코드를 <b>%d분</b> 내에 입력해 주세요.</p><h2>%s</h2>",
                Math.max(1, ttlSeconds/60), code);
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, html);
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("MAIL SEND FAIL: {}", e.getMessage(), e);
        }
    }
}
