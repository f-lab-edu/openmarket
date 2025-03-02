package com.market.openmarket.service;

import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.dto.UserSignUpResponseDto;
import com.market.openmarket.entity.User;
import com.market.openmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByPhone(requestDto.getPhone())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encodedPwd = BCrypt.hashpw(requestDto.getPwd(), BCrypt.gensalt());

        User user = User.builder()
                .email(requestDto.getEmail())
                .pwd(encodedPwd)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .nickname(requestDto.getNickname())
                .address(requestDto.getAddress())
                .isDeleted(false)
                .type(requestDto.getType())
                .build();

        userRepository.save(user);

        return UserSignUpResponseDto.fromEntity(user);
    }
}
