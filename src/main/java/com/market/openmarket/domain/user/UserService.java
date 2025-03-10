package com.market.openmarket.domain.user;

import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.domain.user.entity.User;

public interface UserService {

    User findByIdOrFail(Long id);

    User createUser(UserSignUpRequestDto requestDto);

    User findByEmailOrFail(String email);

    UserResponseDto getUser(Long id);

    UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto);

    void updatePassword(String email, String newPassword);

    void deleteUser(Long id);
}
