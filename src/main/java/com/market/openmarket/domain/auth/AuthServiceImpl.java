package com.market.openmarket.domain.auth;

import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.entity.RefreshToken;
import com.market.openmarket.domain.auth.util.bcrypt.PasswordEncoder;
import com.market.openmarket.domain.auth.util.jwt.JwtProvider;
import com.market.openmarket.domain.user.UserService;
import com.market.openmarket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponseDto signUp(UserSignUpRequestDto requestDto) {
        String hashedPwd = passwordEncoder.hash(requestDto.getPwd());
        requestDto.setPwd(hashedPwd);

        User savedUser = userService.createUser(requestDto);
        return UserResponseDto.fromEntity(savedUser);
    }

    @Transactional
    public UserLogInResponseDto logIn(UserLogInRequestDto requestDto) {
        User user = userService.findByEmail(requestDto.getEmail());

        boolean isValid = passwordEncoder.checkPwd(requestDto.getPwd(), user.getPwd());
        if (!isValid) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        JwtToken accessToken = jwtProvider.generateAccessToken(user);
        JwtToken refreshToken = jwtProvider.generateRefreshToken(user);

        LocalDateTime issuedAt = LocalDateTime.ofInstant(refreshToken.getIssuedAt().toInstant(), ZoneId.of("UTC"));
        LocalDateTime expiresAt = LocalDateTime.ofInstant(refreshToken.getExpiration().toInstant(), ZoneId.of("UTC"));

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> RefreshToken.builder()
                        .userId(user.getId()).build()
                );

        refreshTokenEntity.setToken(refreshToken.getToken());
        refreshTokenEntity.setIssuedAt(issuedAt);
        refreshTokenEntity.setExpiresAt(expiresAt);

        refreshTokenRepository.save(refreshTokenEntity);

        return UserLogInResponseDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
