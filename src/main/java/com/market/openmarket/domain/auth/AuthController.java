package com.market.openmarket.domain.auth;

import com.market.openmarket.common.config.TokenProperties;
import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.common.dto.UserResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> signUp(@Validated @RequestBody UserSignUpRequestDto requestDto) {
        UserResponseDto responseDto = authService.signUp(requestDto);
        URI location = URI.create("/auth/" + responseDto.getId());

        return ResponseEntity.created(location).body(responseDto);
    }

    @PostMapping("/log-in")
    public ResponseEntity<String> logIn(@Validated @RequestBody UserLogInRequestDto requestDto, HttpServletResponse res) {
        UserLogInResponseDto tokens = authService.logIn(requestDto);

        Cookie cookie = new Cookie("refreshToken", tokens.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 24 * 60 * TokenProperties.REFRESH_TOKEN_EXPIRATION_DAYS);
        cookie.setSecure(false);
        res.addCookie(cookie);

        return ResponseEntity.ok(tokens.getAccessToken());
    }
}
