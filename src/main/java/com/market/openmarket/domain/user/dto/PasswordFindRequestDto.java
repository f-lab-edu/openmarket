package com.market.openmarket.domain.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class PasswordFindRequestDto {

    @Email
    private String email;
}
