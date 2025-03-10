package com.market.openmarket.domain.user;

import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.common.util.UserValidator;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.util.bcrypt.PasswordEncoder;
import com.market.openmarket.domain.user.dto.PasswordResetRequestDto;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.domain.user.entity.User;
import com.market.openmarket.domain.user.util.email.EmailService;
import com.market.openmarket.domain.user.util.token.PasswordResetTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserValidator userValidator;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final PasswordResetTokenService passwordResetTokenService;

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

    public void sendPasswordResetEmail(String email) {
        userValidator.checkEmailExists(email);
        emailService.sendPasswordResetEmail(email);
    }

    @Transactional
    public void resetPassword(String email, String token, PasswordResetRequestDto requestDto) {
        String newPwd = requestDto.getNewPwd();
        String confirmPwd = requestDto.getConfirmPwd();
        if (!newPwd.equals(confirmPwd)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        boolean isValid = passwordResetTokenService.validateToken(email, token);
        if (!isValid) {
            throw new IllegalArgumentException("유효하지 않은 비밀번호 재설정 요청입니다.");
        }

        String hashedPwd = passwordEncoder.hash(requestDto.getNewPwd());
        User user = findByEmailOrFail(email);
        user.setPwd(hashedPwd);

        passwordResetTokenService.deleteToken(email);
    }
}
