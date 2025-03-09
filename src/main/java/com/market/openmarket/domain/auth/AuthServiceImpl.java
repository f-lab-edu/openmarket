package com.market.openmarket.domain.auth;

import com.market.openmarket.domain.auth.entity.RefreshToken;
import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.domain.user.entity.User;
import com.market.openmarket.domain.user.UserRepository;
import com.market.openmarket.common.util.UserValidator;
import com.market.openmarket.domain.auth.util.bcrypt.PasswordEncoder;
import com.market.openmarket.domain.auth.util.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponseDto signUp(UserSignUpRequestDto requestDto) {
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

        return UserResponseDto.fromEntity(savedUser);
    }

    @Transactional
    public UserLogInResponseDto logIn(UserLogInRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        boolean isValid = passwordEncoder.checkPwd(requestDto.getPwd(), user.getPwd());
        if (!isValid) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        JwtToken accessToken = jwtProvider.generateAccessToken(user);
        JwtToken refreshToken = jwtProvider.generateRefreshToken(user);

        // TODO: 리팩터링 시 JPA AttributeConverter 적용하기
        LocalDateTime issuedAt = LocalDateTime.ofInstant(refreshToken.getIssuedAt().toInstant(), ZoneId.of("UTC"));
        LocalDateTime expiresAt = LocalDateTime.ofInstant(refreshToken.getExpiration().toInstant(), ZoneId.of("UTC"));

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> RefreshToken.builder()
                        .userId(user.getId()).build()
                );

        // 토큰을 가진 유저가 새로 로그인한다면 토큰 값과 발행일자, 만료일자 새로 세팅
        refreshTokenEntity.setToken(refreshToken.getToken());
        refreshTokenEntity.setIssuedAt(issuedAt);
        refreshTokenEntity.setExpiresAt(expiresAt);

        // 신규 엔티티의 경우 JPA DirtyChecking 적용 안됨. 명시적 save() 호출
        refreshTokenRepository.save(refreshTokenEntity);

        return UserLogInResponseDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
