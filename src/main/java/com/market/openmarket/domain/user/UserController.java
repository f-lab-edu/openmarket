package com.market.openmarket.domain.user;

import com.market.openmarket.domain.user.dto.PasswordFindRequestDto;
import com.market.openmarket.domain.user.dto.UserUpdateRequestDto;
import com.market.openmarket.common.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        UserResponseDto user = userService.getUser(id);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDto requestDto) {
        UserResponseDto updateUser = userService.updateUser(id, requestDto);

        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody PasswordFindRequestDto requestDto) {
        userService.sendPasswordResetEmail(requestDto.getEmail());
        return ResponseEntity.ok("비밀번호 재설정 이메일을 발송했습니다.");
    }
}
