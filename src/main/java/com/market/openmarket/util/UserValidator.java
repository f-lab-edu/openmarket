package com.market.openmarket.util;

import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.exception.DuplicateUserException;
import com.market.openmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

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
}
