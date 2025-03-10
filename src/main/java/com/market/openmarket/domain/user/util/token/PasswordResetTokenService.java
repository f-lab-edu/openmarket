package com.market.openmarket.domain.user.util.token;

import com.market.openmarket.common.config.TokenProperties;
import com.market.openmarket.domain.auth.JwtToken;
import com.market.openmarket.domain.auth.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public String generatePasswordResetToken(String email) {
        JwtToken passwordResetToken = jwtProvider.generatePasswordResetToken(email);
        String token = passwordResetToken.getToken();

        redisTemplate.opsForValue().set(
                email,
                token,
                TokenProperties.PASSWORD_RESET_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        return token;
    }

    public boolean validateToken(String email, String token) {
        String storedToken = redisTemplate.opsForValue().get(email);
        if (storedToken == null || !storedToken.equals(token)) {
            return false;
        }

        try {
            boolean isValid = jwtProvider.validateToken(token);
            if (!isValid) {
                return false;
            }

            String tokenEmail = jwtProvider.getEmailFromToken(token);
            return email.equals(tokenEmail);
        } catch (Exception e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public void deleteToken(String email) {
        redisTemplate.delete(email);
    }
}
