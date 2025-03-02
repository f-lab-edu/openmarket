package com.market.openmarket.controller;

import com.market.openmarket.dto.UserSignUpRequestDto;
import com.market.openmarket.dto.UserSignUpResponseDto;
import com.market.openmarket.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserSignUpResponseDto> signUp(
            @Validated @RequestBody UserSignUpRequestDto requestDto) {
        UserSignUpResponseDto responseDto = userService.signUp(requestDto);
        URI location = URI.create("/auth/" + responseDto.getId());
        return ResponseEntity.created(location).body(responseDto);
    }
}
