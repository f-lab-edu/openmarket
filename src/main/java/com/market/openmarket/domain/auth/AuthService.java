package com.market.openmarket.domain.auth;

import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.common.dto.UserResponseDto;

public interface AuthService {

    UserResponseDto signUp(UserSignUpRequestDto requestDto);

    UserLogInResponseDto logIn(UserLogInRequestDto requestDto);
}
