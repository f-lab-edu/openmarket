package com.market.openmarket.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordResetRequestDto {

    @NotBlank
    private String newPwd;

    @NotBlank
    private String confirmPwd;
}
