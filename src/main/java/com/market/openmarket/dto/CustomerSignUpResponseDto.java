package com.market.openmarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSignUpResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String address;
}
