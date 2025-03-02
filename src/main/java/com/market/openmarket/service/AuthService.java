package com.market.openmarket.service;

import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.dto.UserSignUpResponseDto;
import com.market.openmarket.entity.User;
import com.market.openmarket.repository.UserRepository;
import com.market.openmarket.util.PasswordEncoder;
import com.market.openmarket.util.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    @Transactional
    public UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto) {
        userValidator.validateDuplicate(requestDto);
        String hashedPwd = passwordEncoder.hash(requestDto.getPwd());

        User user = User.builder()
                .email(requestDto.getEmail())
                .pwd(hashedPwd)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .nickname(requestDto.getNickname())
                .address(requestDto.getAddress())
                .isDeleted(false)
                .type(requestDto.getType())
                .build();

        User savedUser = userRepository.save(user);

        return UserSignUpResponseDto.fromEntity(savedUser);
    }
}
