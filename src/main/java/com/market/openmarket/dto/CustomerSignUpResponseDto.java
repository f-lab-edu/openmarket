package com.market.openmarket.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSignUpResponseDto {

    private Integer id;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String address;
}
