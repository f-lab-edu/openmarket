package com.market.openmarket.util;

import com.market.openmarket.config.TokenProperties;
import com.market.openmarket.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String rawKey;

    private Key generateKey() {
        byte[] keyByte = rawKey.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyByte);
    }

    public JwtToken generateAccessToken(User user) {
        Date now = new Date();
        long accessTokenExpiration = 1000 * 60 * TokenProperties.ACCESS_TOKEN_EXPIRATION_MINUTES;
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
        long refreshTokenExpiration = 1000 * 60 * 60 + 24 + TokenProperties.REFRESH_TOKEN_EXPIRATION_DAYS;
        Date expiresDate = new Date(now.getTime() + refreshTokenExpiration);

        String token = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(expiresDate)
                .signWith(generateKey())
                .compact();

        return new JwtToken(token, now, expiresDate);
    }
}
