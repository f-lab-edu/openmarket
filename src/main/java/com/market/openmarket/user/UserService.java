package com.market.openmarket.user;

import com.market.openmarket.dto.request.UserUpdateRequestDto;
import com.market.openmarket.dto.response.UserResponseDto;
import com.market.openmarket.entity.User;

public interface UserService {

    User findByIdOrFail(Long id);

    UserResponseDto getUser(Long id);

    UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto);

    void sendPasswordResetEmail(String email);

    void deleteUser(Long id);
}
