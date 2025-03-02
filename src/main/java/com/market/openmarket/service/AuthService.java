package com.market.openmarket.service;

import com.market.openmarket.dto.UserLogInRequestDto;
import com.market.openmarket.dto.UserLogInResponseDto;
import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.dto.UserSignUpResponseDto;
import com.market.openmarket.entity.RefreshToken;
import com.market.openmarket.entity.User;
import com.market.openmarket.repository.RefreshTokenRepository;
import com.market.openmarket.repository.UserRepository;
import com.market.openmarket.util.JwtToken;
import com.market.openmarket.util.JwtUtil;
import com.market.openmarket.util.PasswordEncoder;
import com.market.openmarket.util.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto) {
        userValidator.validateDuplicate(requestDto);
        String hashedPwd = passwordEncoder.hash(requestDto.getPwd());

        User user = User.builder()
                .email(requestDto.getEmail())
                .pwd(hashedPwd)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .nickname(requestDto.getNickname())
                .address(requestDto.getAddress())
                .isDeleted(false)
                .type(requestDto.getType())
                .build();

        User savedUser = userRepository.save(user);

        return UserSignUpResponseDto.fromEntity(savedUser);
    }

    public UserLogInResponseDto logIn(UserLogInRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        boolean isValid = passwordEncoder.checkPwd(requestDto.getPwd(), user.getPwd());
        if (!isValid) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        JwtToken accessToken = jwtUtil.generateAccessToken(user);
        JwtToken refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken.getToken())
                // TODO: 리팩터링 시 JPA AttributeConverter 적용하기
                .issuedAt(LocalDateTime.ofInstant(refreshToken.getIssuedAt().toInstant(), ZoneId.of("UTC")))
                .expiresAt(LocalDateTime.ofInstant(refreshToken.getExpiration().toInstant(), ZoneId.of("UTC")))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return UserLogInResponseDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
