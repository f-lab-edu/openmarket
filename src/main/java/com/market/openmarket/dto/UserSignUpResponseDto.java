package com.market.openmarket.dto;

import com.market.openmarket.entity.User;
import com.market.openmarket.entity.UserType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpResponseDto {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String address;
    private LocalDateTime createdAt;
    private UserType type;

    public static UserSignUpResponseDto fromEntity(User user) {
        return UserSignUpResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .type(user.getType())
                .build();
    }
}
