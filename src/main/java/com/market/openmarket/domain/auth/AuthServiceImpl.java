package com.market.openmarket.domain.auth;

import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.common.util.UserValidator;
import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.entity.RefreshToken;
import com.market.openmarket.domain.auth.util.bcrypt.PasswordEncoder;
import com.market.openmarket.domain.auth.util.jwt.JwtProvider;
import com.market.openmarket.domain.auth.util.jwt.TokenService;
import com.market.openmarket.domain.user.UserService;
import com.market.openmarket.domain.user.dto.PasswordResetRequestDto;
import com.market.openmarket.domain.user.entity.User;
import com.market.openmarket.domain.user.util.email.EmailService;
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
    private final UserValidator userValidator;
    private final EmailService emailService;
    private final TokenService tokenService;

    @Transactional
    public UserResponseDto signUp(UserSignUpRequestDto requestDto) {
        if (!requestDto.getPwd().equals(requestDto.getConfirmPwd())) {
            throw new IllegalArgumentException("비밀번호를 다시 입력해주세요.");
        }

        String hashedPwd = passwordEncoder.hash(requestDto.getPwd());
        requestDto.setPwd(hashedPwd);

        User savedUser = userService.createUser(requestDto);
        return UserResponseDto.fromEntity(savedUser);
    }

    @Transactional
    public UserLogInResponseDto logIn(UserLogInRequestDto requestDto) {
        User user = userService.findByEmailOrFail(requestDto.getEmail());

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

    public void sendPasswordResetEmail(String email) {
        userValidator.checkEmailExists(email);

        String token = tokenService.savePasswordResetToken(email);
        emailService.sendPasswordResetEmail(email, token);
    }

    public void resetPassword(String email, String token, PasswordResetRequestDto requestDto) {
        userValidator.checkEmailExists(email);
        if (!requestDto.getNewPwd().equals(requestDto.getConfirmPwd())) {
            throw new IllegalArgumentException("비밀번호를 다시 입력해주세요.");
        }

        tokenService.validatePasswordResetToken(email, token);

        String hashedPwd = passwordEncoder.hash(requestDto.getNewPwd());
        userService.updatePassword(email, hashedPwd);
        tokenService.deletePasswordResetToken(email);
    }
}
