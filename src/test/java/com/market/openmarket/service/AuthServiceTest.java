package com.market.openmarket.service;

import com.market.openmarket.config.TokenProperties;
import com.market.openmarket.dto.UserLogInRequestDto;
import com.market.openmarket.dto.UserLogInResponseDto;
import com.market.openmarket.dto.UserResponseDto;
import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.entity.User;
import com.market.openmarket.entity.UserType;
import com.market.openmarket.exception.DuplicateUserException;
import com.market.openmarket.repository.RefreshTokenRepository;
import com.market.openmarket.repository.UserRepository;
import com.market.openmarket.util.JwtToken;
import com.market.openmarket.util.JwtUtil;
import com.market.openmarket.util.PasswordEncoder;
import com.market.openmarket.util.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

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
        User savedUser = User.builder()
                .id(1L)
                .email(signUpRequestDto.getEmail())
                .pwd(hashedPwd)
                .name(signUpRequestDto.getName())
                .phone(signUpRequestDto.getPhone())
                .nickname(signUpRequestDto.getNickname())
                .address(signUpRequestDto.getAddress())
                .isDeleted(false)
                .type(signUpRequestDto.getType())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto responseDto = authService.signUp(signUpRequestDto);

        assertEquals(1L, responseDto.getId());
        assertEquals(signUpRequestDto.getEmail(), responseDto.getEmail());
        assertEquals(signUpRequestDto.getName(), responseDto.getName());
        assertEquals(signUpRequestDto.getPhone(), responseDto.getPhone());
        assertEquals(signUpRequestDto.getNickname(), responseDto.getNickname());
        assertEquals(signUpRequestDto.getAddress(), responseDto.getAddress());
        assertEquals(signUpRequestDto.getType(), responseDto.getType());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUpFailed() {
        doThrow(new DuplicateUserException("Duplicate email")).when(userValidator).validateDuplicate(signUpRequestDto);

        assertThrows(DuplicateUserException.class, () -> {
            authService.signUp(signUpRequestDto);
        });
    }

    @Test
    @DisplayName("로그인 성공")
    void logIn() {
        when(userRepository.findByEmail(logInRequestDto.getEmail()))
                .thenReturn(Optional.of(fakeUser));

        when(passwordEncoder.checkPwd(logInRequestDto.getPwd(), fakeUser.getPwd()))
                .thenReturn(true);

        Date now = new Date();
        Date accessExpiresAt = new Date(now.getTime() + 1000 * 60 * TokenProperties.ACCESS_TOKEN_EXPIRATION_MINUTES);
        Date refreshExpiresAt = new Date(now.getTime() + 1000 * 60 * 60 * 24 * TokenProperties.REFRESH_TOKEN_EXPIRATION_DAYS);
        JwtToken dummyAccessToken = new JwtToken("access-token", now, accessExpiresAt);
        JwtToken dummyRefreshToken = new JwtToken("refresh-token", now, refreshExpiresAt);

        when(jwtUtil.generateAccessToken(fakeUser)).thenReturn(dummyAccessToken);
        when(jwtUtil.generateRefreshToken(fakeUser)).thenReturn(dummyRefreshToken);
        when(refreshTokenRepository.findByUserId(fakeUser.getId()))
                .thenReturn(Optional.empty());

        UserLogInResponseDto responseDto = authService.logIn(logInRequestDto);

        assertEquals("access-token", responseDto.getAccessToken());
        assertEquals("refresh-token", responseDto.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 - 회원 없음")
    void LogInFailedByNoUser() {
        logInRequestDto.setEmail("wrong@wrong.com");

        when(userRepository.findByEmail(logInRequestDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            authService.logIn(logInRequestDto);
        });
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void logInFailedByPwd() {
        logInRequestDto.setPwd("wrong");

        when(userRepository.findByEmail(logInRequestDto.getEmail())).thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.checkPwd(logInRequestDto.getPwd(), fakeUser.getPwd())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.logIn(logInRequestDto);
        });
    }
}
