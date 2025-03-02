package com.market.openmarket.dto;

import com.market.openmarket.entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequestDto {

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String pwd;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Phone is required.")
    private String phone;

    @NotBlank(message = "Nickname is required.")
    private String nickname;

    @NotBlank(message = "Address is required.")
    private String address;

    @NotNull(message = "User type is required.")
    private UserType type;
}
