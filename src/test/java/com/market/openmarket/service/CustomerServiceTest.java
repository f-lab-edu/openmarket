package com.market.openmarket.service;

import com.market.openmarket.dto.CustomerSignUpRequestDto;
import com.market.openmarket.dto.CustomerSignUpResponseDto;
import com.market.openmarket.entity.Customer;
import com.market.openmarket.exception.EmailExistException;
import com.market.openmarket.exception.NicknameExistException;
import com.market.openmarket.exception.PhoneExistException;
import com.market.openmarket.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerSignUpRequestDto requestDto;

    @BeforeEach
    void beforeEach() {
        requestDto = new CustomerSignUpRequestDto();
        requestDto.setEmail("annonymous@qwer.com");
        requestDto.setPwd("abc1234");
        requestDto.setName("YUNJAE");
        requestDto.setPhone("010-1234-5678");
        requestDto.setNickname("윤재조");
        requestDto.setAddress("서울특별시 관악구");
    }

    @Test
    void signUp() {
        CustomerSignUpResponseDto responseDto = customerService.signUp(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(responseDto.getNickname()).isEqualTo(requestDto.getNickname());

        Customer savedCustomer = customerRepository.findByEmail(requestDto.getEmail()).orElse(null);
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getEmail()).isEqualTo(requestDto.getEmail());
    }

    @Test
    void signUp_duplicated_email() {
        customerService.signUp(requestDto);
        requestDto.setNickname("다른닉네임");
        requestDto.setPhone("01033339999");

        assertThrows(EmailExistException.class, () -> {
            customerService.signUp(requestDto);
        });
    }

    @Test
    void signUp_duplicated_nickname() {
        customerService.signUp(requestDto);
        requestDto.setEmail("other-email@asdf.com");
        requestDto.setPhone("01033339999");

        assertThrows(NicknameExistException.class, () -> {
            customerService.signUp(requestDto);
        });
    }

    @Test
    void signUp_duplicated_phone() {
        customerService.signUp(requestDto);
        requestDto.setEmail("other-email@asdf.com");
        requestDto.setNickname("다른닉네임");

        assertThrows(PhoneExistException.class, () -> {
            customerService.signUp(requestDto);
        });
    }
}
