package com.market.openmarket.domain.user;

import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.domain.user.entity.User;

public interface UserService {

    User findByIdOrFail(Long id);

    User createUser(UserSignUpRequestDto requestDto);

    User findByEmail(String email);

    UserResponseDto getUser(Long id);

    UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto);

    void sendPasswordResetEmail(String email);

    void deleteUser(Long id);
}
