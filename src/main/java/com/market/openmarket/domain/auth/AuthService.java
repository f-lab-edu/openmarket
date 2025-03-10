package com.market.openmarket.domain.auth;

import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.user.dto.PasswordResetRequestDto;

public interface AuthService {

    UserResponseDto signUp(UserSignUpRequestDto requestDto);

    UserLogInResponseDto logIn(UserLogInRequestDto requestDto);

    void sendPasswordResetEmail(String email);

    void resetPassword(String email, String token, PasswordResetRequestDto requestDto);
}
