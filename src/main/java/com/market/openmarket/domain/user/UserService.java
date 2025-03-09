package com.market.openmarket.domain.user;

import com.market.openmarket.domain.user.entity.User;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.common.dto.UserResponseDto;

public interface UserService {

    User findByIdOrFail(Long id);

    UserResponseDto getUser(Long id);

    UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto);

    void sendPasswordResetEmail(String email);

    void deleteUser(Long id);
}
