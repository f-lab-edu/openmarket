package com.market.openmarket.controller;

import com.market.openmarket.dto.CustomerSignUpRequestDto;
import com.market.openmarket.dto.CustomerSignUpResponseDto;
import com.market.openmarket.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;


@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("auth")
    public ResponseEntity<CustomerSignUpResponseDto> signUp(@Validated @RequestBody CustomerSignUpRequestDto dto) {
        CustomerSignUpResponseDto response = customerService.signUp(dto);
        URI location = URI.create("api/v1/customers/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }
}
