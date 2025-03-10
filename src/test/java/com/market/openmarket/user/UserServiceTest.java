package com.market.openmarket.user;

import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.user.UserRepository;
import com.market.openmarket.domain.user.UserServiceImpl;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.domain.user.entity.User;
import com.market.openmarket.domain.user.entity.UserType;
import com.market.openmarket.common.exception.DuplicateUserException;
import com.market.openmarket.common.util.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private User fakeUser;
    private UserSignUpRequestDto signUpRequestDto;

    @BeforeEach
    void setUp() {
        fakeUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .pwd("hashedPassword123")
                .name("jo")
                .phone("010-1234-5678")
                .nickname("jojo")
                .address("test-address")
                .isDeleted(false)
                .type(UserType.CUSTOMER)
                .build();

        signUpRequestDto = UserSignUpRequestDto.builder()
                .email("test@test.com")
                .pwd("1234")
                .build();
    }

    @Test
    @DisplayName("유저를 id 로 조회한다.")
    void findByIdOrFail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        User user = userService.findByIdOrFail(1L);

        assertThat(user.getId()).isEqualTo(fakeUser.getId());
        assertThat(user.getEmail()).isEqualTo(fakeUser.getEmail());
        assertThat(user.getName()).isEqualTo(fakeUser.getName());
        assertThat(user.getPhone()).isEqualTo(fakeUser.getPhone());
        assertThat(user.getNickname()).isEqualTo(fakeUser.getNickname());
        assertThat(user.getAddress()).isEqualTo(fakeUser.getAddress());
    }

    @Test
    @DisplayName("유저를 id 로 조회하기에 실패한다.")
    void findByIdOrFailFailed() {
        when(userRepository.findById(1L)).thenThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> userService.findByIdOrFail(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("유저를 이메일로 조회한다.")
    void findByEmailOrFail() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(fakeUser));
        User user = userService.findByEmailOrFail("test@test.com");

        assertThat(user.getId()).isEqualTo(fakeUser.getId());
    }

    @Test
    @DisplayName("유저를 이메일로 조회하기에 실패한다.")
    void findByEmailOrFailFailed() {
        when(userRepository.findByEmail("wrong@test.com")).thenThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> userService.findByEmailOrFail("wrong@test.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("유저 정보를 가져온다.")
    void getUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        UserResponseDto responseDto = userService.getUser(1L);

        assertThat(fakeUser.getId()).isEqualTo(responseDto.getId());
        assertThat(fakeUser.getEmail()).isEqualTo(responseDto.getEmail());
        assertThat(fakeUser.getName()).isEqualTo(responseDto.getName());
        assertThat(fakeUser.getPhone()).isEqualTo(responseDto.getPhone());
        assertThat(fakeUser.getNickname()).isEqualTo(responseDto.getNickname());
        assertThat(fakeUser.getAddress()).isEqualTo(responseDto.getAddress());
    }

    @Test
    @DisplayName("유저 정보를 가져오기에 실패한다.")
    void getUserFailed() {
        when(userRepository.findById(1L)).thenThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> userService.getUser(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 생성에 성공한다.")
    void createUser() {
        doNothing().when(userValidator).validateDuplicate(signUpRequestDto);
        when(userRepository.save(any(User.class))).thenReturn(fakeUser);

        User user = userService.createUser(signUpRequestDto);

        assertThat(user.getId()).isEqualTo(fakeUser.getId());
        assertThat(user.getEmail()).isEqualTo(fakeUser.getEmail());
        assertThat(user.getName()).isEqualTo(fakeUser.getName());
        assertThat(user.getPhone()).isEqualTo(fakeUser.getPhone());
        assertThat(user.getNickname()).isEqualTo(fakeUser.getNickname());
        assertThat(user.getAddress()).isEqualTo(fakeUser.getAddress());
        assertThat(user.getType()).isEqualTo(fakeUser.getType());
    }

    @Test
    @DisplayName("회원 생성에 실패 - 중복된 정보")
    void createUserFailedByDuplicatedEmail() {
        doThrow(DuplicateUserException.class).when(userValidator).validateDuplicate(signUpRequestDto);

        assertThatThrownBy(() -> userService.createUser(signUpRequestDto))
                .isInstanceOf(DuplicateUserException.class);
    }

    @Test
    @DisplayName("회원 정보 수정에 성공한다.")
    void updateUser() {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setNickname("koko");
        requestDto.setPhone("010-2345-3456");
        requestDto.setAddress("changed-address");

        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        doNothing().when(userValidator).validateDuplicateForUpdate(1L, requestDto);

        UserResponseDto responseDto = userService.updateUser(1L, requestDto);

        assertThat(responseDto.getNickname()).isEqualTo("koko");
        assertThat(responseDto.getPhone()).isEqualTo("010-2345-3456");
        assertThat(responseDto.getAddress()).isEqualTo("changed-address");
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 닉네임 중복")
    void updateUserFailedByDuplicatedNickname() {
        fakeUser.setNickname("dup");
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setNickname("dup");

        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        doThrow(DuplicateUserException.class).when(userValidator).validateDuplicateForUpdate(1L, updateDto);

        assertThatThrownBy(() -> userService.updateUser(1L, updateDto))
                .isInstanceOf(DuplicateUserException.class);
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 전화번호 중복")
    void updateUserFailedByDuplicatedPhone() {
        fakeUser.setPhone("010-5555-5555");
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setNickname("010-5555-5555");

        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        doThrow(DuplicateUserException.class).when(userValidator).validateDuplicateForUpdate(1L, updateDto);

        assertThatThrownBy(() -> userService.updateUser(1L, updateDto))
                .isInstanceOf(DuplicateUserException.class);
    }

    @Test
    @DisplayName("회원 탈퇴에 성공한다(SOFT DELETE)")
    void deleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));

        userService.deleteUser(1L);

        assertNull(fakeUser.getEmail());
        assertNull(fakeUser.getNickname());
        assertNull(fakeUser.getPhone());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 유저 조회 실패")
    void deleteUserFailed() {
        when(userRepository.findById(1L)).thenThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
