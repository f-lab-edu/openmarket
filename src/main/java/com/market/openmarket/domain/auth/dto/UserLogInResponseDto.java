package com.market.openmarket.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLogInResponseDto {

    String accessToken;

    String refreshToken;
}
