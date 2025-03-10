package com.market.openmarket.domain.auth.util.jwt;

import com.market.openmarket.common.config.TokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public String savePasswordResetToken(String email) {
        String token = jwtProvider.generatePasswordResetToken(email);
        redisTemplate.opsForValue().set(
                email,
                token,
                TokenProperties.PASSWORD_RESET_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        return token;
    }

    public void validatePasswordResetToken(String email, String token) {
        String storedToken = redisTemplate.opsForValue().get(email);
        if (storedToken == null || !storedToken.equals(token)) {
            throw new IllegalArgumentException("유효하지 않은 비밀번호 재설정 토큰입니다.");
        }

        try {
            boolean isValid = jwtProvider.validateToken(token);
            if (!isValid) {
                throw new IllegalArgumentException("비밀번호 재설정 토큰이 만료되었습니다.");
            }

            String tokenEmail = jwtProvider.getSubjectFromToken(token);
            if (!tokenEmail.equals(email)) {
                throw new IllegalArgumentException("토큰과 이메일이 일치하지 않습니다.");
            }
        } catch (Exception e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
        }
    }

    public void deletePasswordResetToken(String email) {
        redisTemplate.delete(email);
    }
}
