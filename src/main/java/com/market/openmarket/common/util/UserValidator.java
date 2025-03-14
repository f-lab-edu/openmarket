package com.market.openmarket.common.util;

import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.common.exception.DuplicateUserException;
import com.market.openmarket.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void validateDuplicate(UserSignUpRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateUserException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new DuplicateUserException("이미 사용 중인 전화번호입니다.");
        }
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateUserException("이미 사용 중인 닉네임입니다.");
        }
    }

    @Transactional(readOnly = true)
    public void validateDuplicateForUpdate(Long userId, UserUpdateRequestDto requestDto) {
        if (requestDto.getNickname() != null) {
            userRepository.findByNickname(requestDto.getNickname())
                    .filter(user -> !user.getId().equals(userId))
                    .ifPresent(user -> {
                        throw new DuplicateUserException("이미 사용 중인 닉네임입니다.");
                    });
        }
        if (requestDto.getPhone() != null) {
            userRepository.findByPhone(requestDto.getPhone())
                    .filter(user -> !user.getId().equals(userId))
                    .ifPresent(user -> {
                        throw new DuplicateUserException("이미 사용 중인 전화번호입니다.");
                    });
        }
    }

    @Transactional(readOnly = true)
    public void checkEmailExists(String email) {
        boolean isExist = userRepository.existsByEmail(email);
        if (!isExist) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }
    }
}
