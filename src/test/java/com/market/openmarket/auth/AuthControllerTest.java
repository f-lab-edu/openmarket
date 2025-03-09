package com.market.openmarket.auth;

import com.market.openmarket.domain.auth.AuthController;
import com.market.openmarket.domain.auth.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("회원가입 성공")
    void signUp() {
        // TODO: MockMvc 조사 후 구현하기
        Assertions.fail("TODO: MockMvc 찾아보기");
    }
}
