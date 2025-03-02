package com.market.openmarket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLogInResponseDto {

    String accessToken;

    String refreshToken;
}
