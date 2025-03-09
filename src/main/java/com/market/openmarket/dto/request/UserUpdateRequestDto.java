package com.market.openmarket.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {

    private String nickname;

    private String phone;

    private String address;
}
