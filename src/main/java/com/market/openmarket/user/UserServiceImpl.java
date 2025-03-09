package com.market.openmarket.user;

import com.market.openmarket.dto.request.UserUpdateRequestDto;
import com.market.openmarket.dto.response.UserResponseDto;
import com.market.openmarket.entity.User;
import com.market.openmarket.util.UserValidator;
import com.market.openmarket.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    private final EmailService emailService;

    @Transactional
    public User findByIdOrFail(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    @Transactional
    public UserResponseDto getUser(Long id) {
        User user = findByIdOrFail(id);

        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto) {
        User user = findByIdOrFail(id);

        userValidator.validateDuplicateForUpdate(id, requestDto);

        if (requestDto.getNickname() != null) {
            user.setNickname(requestDto.getNickname());
        }
        if (requestDto.getPhone() != null) {
            user.setPhone(requestDto.getPhone());
        }
        if (requestDto.getAddress() != null) {
            user.setAddress(requestDto.getAddress());
        }

        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findByIdOrFail(id);

        user.setIsDeleted(true);
        user.setEmail(null);
        user.setNickname(null);
        user.setPhone(null);
        // TODO: Postgres 의 경우, partial unique index 를 사용하여 해결 가능함.
    }

    @Transactional
    public void sendPasswordResetEmail(String email) {
        userRepository.existsByEmail(email);

        // TODO: 운영환경 주소 변경
        emailService.sendPasswordResetEmail(email);
    }
}
