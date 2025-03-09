package com.market.openmarket.common.dto;

import com.market.openmarket.domain.user.entity.User;
import com.market.openmarket.domain.user.entity.UserType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String address;
    private UserType type;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .address(user.getAddress())
                .type(user.getType())
                .build();
    }
}
