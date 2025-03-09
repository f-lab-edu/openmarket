package com.market.openmarket.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLogInResponseDto {

    String accessToken;

    String refreshToken;
}
