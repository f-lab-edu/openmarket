package com.market.openmarket.service;

import com.market.openmarket.dto.CustomerSignUpRequestDto;
import com.market.openmarket.dto.CustomerSignUpResponseDto;
import com.market.openmarket.entity.Customer;
import com.market.openmarket.exception.EmailExistException;
import com.market.openmarket.exception.NicknameExistException;
import com.market.openmarket.exception.PhoneExistException;
import com.market.openmarket.repository.CustomerRepository;
import com.market.openmarket.util.PasswordEncoder;
import com.market.openmarket.util.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public CustomerSignUpResponseDto signUp(CustomerSignUpRequestDto dto) {
        validator.customerSignUpValidate(dto);

        Customer customer = customerRepository.save(Customer.builder()
                .email(dto.getEmail())
                .pwd(passwordEncoder.hashPassword(dto.getPwd()))
                .name(dto.getName())
                .phone(dto.getPhone())
                .nickname(dto.getNickname())
                .address(dto.getAddress())
                .build());

        return CustomerSignUpResponseDto.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .name(customer.getName())
                .phone(customer.getPhone())
                .nickname(customer.getNickname())
                .address(customer.getAddress())
                .build();
    }
}
