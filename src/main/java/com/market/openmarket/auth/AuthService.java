package com.market.openmarket.auth;

import com.market.openmarket.dto.request.UserLogInRequestDto;
import com.market.openmarket.dto.request.UserSignUpRequestDto;
import com.market.openmarket.dto.response.UserLogInResponseDto;
import com.market.openmarket.dto.response.UserResponseDto;

public interface AuthService {

    UserResponseDto signUp(UserSignUpRequestDto requestDto);

    UserLogInResponseDto logIn(UserLogInRequestDto requestDto);
}
