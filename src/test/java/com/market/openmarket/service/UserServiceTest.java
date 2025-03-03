package com.market.openmarket.service;

import com.market.openmarket.dto.UserResponseDto;
import com.market.openmarket.dto.UserUpdateRequestDto;
import com.market.openmarket.entity.User;
import com.market.openmarket.entity.UserType;
import com.market.openmarket.exception.DuplicateUserException;
import com.market.openmarket.repository.UserRepository;
import com.market.openmarket.util.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    private User fakeUser;

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
    }

    @Test
    @DisplayName("유저를 id로 찾는다.")
    void getUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        UserResponseDto responseDto = userService.getUser(1L);

        assertEquals(fakeUser.getId(), responseDto.getId());
        assertEquals(fakeUser.getEmail(), responseDto.getEmail());
        assertEquals(fakeUser.getName(), responseDto.getName());
        assertEquals(fakeUser.getPhone(), responseDto.getPhone());
        assertEquals(fakeUser.getNickname(), responseDto.getNickname());
        assertEquals(fakeUser.getAddress(), responseDto.getAddress());
    }

    @Test
    @DisplayName("유저를 id로 찾는데 실패한다.")
    void getUserFailed() {
        when(userRepository.findById(1L)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUser(1L);
        });
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

        assertEquals("koko", responseDto.getNickname());
        assertEquals("010-2345-3456", responseDto.getPhone());
        assertEquals("changed-address", responseDto.getAddress());
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 닉네임 중복")
    void updateUserFailedByDuplicatedNickname() {
        fakeUser.setNickname("dup");
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setNickname("dup");

        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        doThrow(DuplicateUserException.class).when(userValidator).validateDuplicateForUpdate(1L, updateDto);

        assertThrows(DuplicateUserException.class, () ->
                userService.updateUser(1L, updateDto));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 전화번호 중복")
    void updateUserFailedByDuplicatedPhone() {
        fakeUser.setPhone("010-5555-5555");
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setNickname("010-5555-5555");

        when(userRepository.findById(1L)).thenReturn(Optional.of(fakeUser));
        doThrow(DuplicateUserException.class).when(userValidator).validateDuplicateForUpdate(1L, updateDto);

        assertThrows(DuplicateUserException.class, () ->
                userService.updateUser(1L, updateDto));
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

        assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(1L);
        });
    }
}
