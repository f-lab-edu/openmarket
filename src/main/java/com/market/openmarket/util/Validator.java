package com.market.openmarket.util;

import com.market.openmarket.dto.CustomerSignUpRequestDto;
import com.market.openmarket.exception.EmailExistException;
import com.market.openmarket.exception.NicknameExistException;
import com.market.openmarket.exception.PhoneExistException;
import com.market.openmarket.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Validator {

    private final CustomerRepository customerRepository;

    public void customerSignUpValidate(CustomerSignUpRequestDto dto) {
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistException();
        }
        if (customerRepository.existsByNickname(dto.getNickname())) {
            throw new NicknameExistException();
        }
        if (customerRepository.existsByPhone(dto.getPhone())) {
            throw new PhoneExistException();
        }
    }
}
