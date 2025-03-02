package com.market.openmarket.service;

import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.dto.UserSignUpResponseDto;
import com.market.openmarket.entity.User;
import com.market.openmarket.entity.UserType;
import com.market.openmarket.exception.DuplicateUserException;
import com.market.openmarket.repository.UserRepository;
import com.market.openmarket.util.PasswordEncoder;
import com.market.openmarket.util.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private AuthService authService;

    private UserSignUpRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = UserSignUpRequestDto.builder()
                .email("test@test.com")
                .pwd("1234")
                .name("jo")
                .phone("010-1234-5678")
                .nickname("jojo")
                .address("test-address")
                .type(UserType.CUSTOMER)
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp() {
        String hashedPwd = "hashedPassword123";
        when(passwordEncoder.hash(requestDto.getPwd())).thenReturn(hashedPwd);
        User savedUser = User.builder()
                .id(1L)
                .email(requestDto.getEmail())
                .pwd(hashedPwd)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .nickname(requestDto.getNickname())
                .address(requestDto.getAddress())
                .isDeleted(false)
                .type(requestDto.getType())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserSignUpResponseDto responseDto = authService.signUp(requestDto);

        assertEquals(1L, responseDto.getId());
        assertEquals(requestDto.getEmail(), responseDto.getEmail());
        assertEquals(requestDto.getName(), responseDto.getName());
        assertEquals(requestDto.getPhone(), responseDto.getPhone());
        assertEquals(requestDto.getNickname(), responseDto.getNickname());
        assertEquals(requestDto.getAddress(), responseDto.getAddress());
        assertEquals(requestDto.getType(), responseDto.getType());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUpFailed() {
        doThrow(new DuplicateUserException("Duplicate email")).when(userValidator).validateDuplicate(requestDto);

        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () -> {
            authService.signUp(requestDto);
        });
        assertEquals("Duplicate email", exception.getMessage());
    }
}
