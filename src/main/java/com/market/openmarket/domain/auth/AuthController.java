package com.market.openmarket.domain.auth;

import com.market.openmarket.common.config.TokenProperties;
import com.market.openmarket.common.dto.UserResponseDto;
import com.market.openmarket.domain.auth.dto.UserLogInRequestDto;
import com.market.openmarket.domain.auth.dto.UserLogInResponseDto;
import com.market.openmarket.domain.auth.dto.UserSignUpRequestDto;
import com.market.openmarket.domain.user.dto.PasswordFindRequestDto;
import com.market.openmarket.domain.user.dto.PasswordResetRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/password-reset/request")
    public ResponseEntity<String> sendPasswordResetEmail(@RequestBody PasswordFindRequestDto requestDto) {
        authService.sendPasswordResetEmail(requestDto.getEmail());
        return ResponseEntity.ok("비밀번호 재설정 이메일을 발송했습니다.");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> resetPassword(
            @RequestParam("email") String email,
            @RequestParam("token") String token,
            @RequestBody PasswordResetRequestDto requestDto
    ) {
        authService.resetPassword(email, token, requestDto);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }
}
