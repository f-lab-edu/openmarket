package com.market.openmarket.auth;

import com.market.openmarket.common.config.TokenProperties;
import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.common.exception.DuplicateUserException;
import com.market.openmarket.domain.auth.AuthServiceImpl;
import com.market.openmarket.domain.auth.JwtToken;
import com.market.openmarket.domain.auth.RefreshTokenRepository;
import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.util.bcrypt.PasswordEncoder;
import com.market.openmarket.domain.auth.util.jwt.JwtProvider;
import com.market.openmarket.domain.user.UserService;
import com.market.openmarket.domain.user.entity.User;
import com.market.openmarket.domain.user.entity.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserSignUpRequestDto signUpRequestDto;
    private User fakeUser;
    private UserLogInRequestDto logInRequestDto;

    @BeforeEach
    void setUp() {
        signUpRequestDto = UserSignUpRequestDto.builder()
                .email("test@test.com")
                .pwd("1234")
                .name("jo")
                .phone("010-1234-5678")
                .nickname("jojo")
                .address("test-address")
                .type(UserType.CUSTOMER)
                .build();

        fakeUser = User.builder()
                .id(1L)
                .email(signUpRequestDto.getEmail())
                .pwd("hashedPassword123")
                .name(signUpRequestDto.getName())
                .phone(signUpRequestDto.getPhone())
                .nickname(signUpRequestDto.getNickname())
                .address(signUpRequestDto.getAddress())
                .isDeleted(false)
                .type(signUpRequestDto.getType())
                .build();

        logInRequestDto = UserLogInRequestDto.builder()
                .email("test@test.com")
                .pwd("1234")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp() {
        String hashedPwd = "hashedPassword123";
        when(passwordEncoder.hash(signUpRequestDto.getPwd())).thenReturn(hashedPwd);
        when(userService.createUser(any(UserSignUpRequestDto.class))).thenReturn(fakeUser);

        UserResponseDto responseDto = authService.signUp(signUpRequestDto);

        assertThat(responseDto.getId()).isEqualTo(fakeUser.getId());
        assertThat(responseDto.getEmail()).isEqualTo(fakeUser.getEmail());
        assertThat(responseDto.getName()).isEqualTo(fakeUser.getName());
        assertThat(responseDto.getPhone()).isEqualTo(fakeUser.getPhone());
        assertThat(responseDto.getNickname()).isEqualTo(fakeUser.getNickname());
        assertThat(responseDto.getAddress()).isEqualTo(fakeUser.getAddress());
        assertThat(responseDto.getType()).isEqualTo(fakeUser.getType());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUpFailed() {
        when(userService.createUser(any(UserSignUpRequestDto.class)))
                .thenThrow(new DuplicateUserException("이미 사용 중인 이메일입니다."));

        assertThatThrownBy(() -> authService.signUp(signUpRequestDto))
                .isInstanceOf(DuplicateUserException.class);
    }

    @Test
    @DisplayName("로그인 성공")
    void logIn() {
        when(userService.findByEmailOrFail(logInRequestDto.getEmail()))
                .thenReturn(fakeUser);
        when(passwordEncoder.checkPwd(logInRequestDto.getPwd(), fakeUser.getPwd()))
                .thenReturn(true);

        Date now = new Date();
        Date accessExpiresAt = new Date(now.getTime() + 1000 * 60 * TokenProperties.ACCESS_TOKEN_EXPIRATION_MINUTES);
        Date refreshExpiresAt = new Date(now.getTime() + 1000 * 60 * 60 * 24 * TokenProperties.REFRESH_TOKEN_EXPIRATION_DAYS);
        JwtToken dummyAccessToken = new JwtToken("access-token", now, accessExpiresAt);
        JwtToken dummyRefreshToken = new JwtToken("refresh-token", now, refreshExpiresAt);

        when(jwtProvider.generateAccessToken(fakeUser)).thenReturn(dummyAccessToken);
        when(jwtProvider.generateRefreshToken(fakeUser)).thenReturn(dummyRefreshToken);
        when(refreshTokenRepository.findByUserId(fakeUser.getId()))
                .thenReturn(Optional.empty());

        UserLogInResponseDto responseDto = authService.logIn(logInRequestDto);

        assertThat("access-token").isEqualTo(responseDto.getAccessToken());
        assertThat("refresh-token").isEqualTo(responseDto.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 - 회원 없음")
    void LogInFailedByNoUser() {
        logInRequestDto.setEmail("wrong@wrong.com");

        when(userService.findByEmailOrFail(logInRequestDto.getEmail())).thenThrow(new IllegalArgumentException("존재하지 않는 사용자입니다."));

        assertThatThrownBy(() -> authService.logIn(logInRequestDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void logInFailedByPwd() {
        logInRequestDto.setPwd("wrong");

        when(userService.findByEmailOrFail(logInRequestDto.getEmail())).thenReturn(fakeUser);
        when(passwordEncoder.checkPwd(logInRequestDto.getPwd(), fakeUser.getPwd())).thenReturn(false);

        assertThatThrownBy(() -> authService.logIn(logInRequestDto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
