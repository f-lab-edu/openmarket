package com.market.openmarket.controller;

import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.dto.UserSignUpResponseDto;
import com.market.openmarket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpResponseDto> signUp(
            @Validated @RequestBody UserSignUpRequestDto requestDto) {
        UserSignUpResponseDto responseDto = userService.signUp(requestDto);
        URI location = URI.create("/api/v1/users/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }
}
