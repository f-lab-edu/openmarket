package com.market.openmarket.service;

import com.market.openmarket.dto.CustomerSignUpRequestDto;
import com.market.openmarket.dto.CustomerSignUpResponseDto;
import com.market.openmarket.entity.Customer;
import com.market.openmarket.repository.CustomerRepository;
import com.market.openmarket.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerSignUpResponseDto signUp(CustomerSignUpRequestDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (customerRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

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
