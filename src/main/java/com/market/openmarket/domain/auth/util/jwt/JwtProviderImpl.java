package com.market.openmarket.domain.auth.util.jwt;

import com.market.openmarket.domain.auth.JwtToken;
import com.market.openmarket.common.config.TokenProperties;
import com.market.openmarket.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProviderImpl implements JwtProvider {

    @Value("${jwt.secret}")
    private String rawKey;

    private SecretKey generateKey() {
        byte[] keyByte = rawKey.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyByte);
    }

    public JwtToken generateAccessToken(User user) {
        Date now = new Date();
        long accessTokenExpiration = 1000L * 60 * TokenProperties.ACCESS_TOKEN_EXPIRATION_MINUTES;
        Date expiresDate = new Date(now.getTime() + accessTokenExpiration);

        String token = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(expiresDate)
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .signWith(generateKey())
                .compact();

        return new JwtToken(token, now, expiresDate);
    }

    public JwtToken generateRefreshToken(User user) {
        Date now = new Date();
        long refreshTokenExpiration = 1000L * 60 * 60 + 24 + TokenProperties.REFRESH_TOKEN_EXPIRATION_DAYS;
        Date expiresDate = new Date(now.getTime() + refreshTokenExpiration);

        String token = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(expiresDate)
                .signWith(generateKey())
                .compact();

        return new JwtToken(token, now, expiresDate);
    }

    public JwtToken generatePasswordResetToken(String email) {
        Date now = new Date();
        long passwordResetTokenExpiration = 1000L * 60 * TokenProperties.PASSWORD_RESET_EXPIRATION_MINUTES;
        Date expiresDate = new Date(now.getTime() + passwordResetTokenExpiration);

        String token = Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiresDate)
                .claim("purpose", "password-reset")
                .signWith(generateKey())
                .compact();

        return new JwtToken(token, now, expiresDate);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            log.warn("토큰에서 이메일 추출 실패: {}", e.getMessage());
            return null;
        }
    }
}
