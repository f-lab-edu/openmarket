package com.market.openmarket.controller;

import com.market.openmarket.dto.CustomerSignUpRequestDto;
import com.market.openmarket.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("auth")
    public void signUp(@Validated @RequestBody CustomerSignUpRequestDto dto) {
        customerService.signUp(dto);
    }
}
