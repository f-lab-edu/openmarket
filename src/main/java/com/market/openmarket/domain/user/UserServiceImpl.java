package com.market.openmarket.domain.user;

import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.common.util.UserValidator;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.util.bcrypt.PasswordEncoder;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findByIdOrFail(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    @Transactional
    public User createUser(UserSignUpRequestDto requestDto) {
        userValidator.validateDuplicate(requestDto);

        User user = User.builder()
                .email(requestDto.getEmail())
                .pwd(requestDto.getPwd())
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .nickname(requestDto.getNickname())
                .address(requestDto.getAddress())
                .isDeleted(false)
                .type(requestDto.getType())
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmailOrFail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Transactional(readOnly = true)
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
    }

    @Transactional
    public void updatePassword(String email, String pwd) {
        User user = findByEmailOrFail(email);
        user.setPwd(pwd);
    }
}
