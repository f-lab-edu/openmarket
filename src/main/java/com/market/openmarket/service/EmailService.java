package com.market.openmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendPasswordResetEmail(String email) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("OOPEN MARKET - 비밀번호 찾기");
            // TODO: setText 내용 수정(버튼으로 등)
            simpleMailMessage.setText("아래 버튼을 누르면 비밀번호 재설정으로 이동합니다.");

            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            log.warn(e.getMessage());
        }
    }
}
