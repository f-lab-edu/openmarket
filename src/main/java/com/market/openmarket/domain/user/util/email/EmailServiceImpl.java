package com.market.openmarket.domain.user.util.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${application.url}")
    private String baseUrl;

    public void sendPasswordResetEmail(String email, String token) {
        try {
            String resetUrl = baseUrl + "/password-reset?token=" + token;

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("OOPEN MARKET - 비밀번호 찾기");
            simpleMailMessage.setText("아래 링크를 클릭하여 비밀번호를 재설정해주세요. \n" + resetUrl);

            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            log.warn(e.getMessage());
            throw new RuntimeException("이메일 전송에 실패했습니다.");
        }
    }
}
