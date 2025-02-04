package com.market.openmarket.service;

import com.market.openmarket.dto.CustomerSignUpRequestDto;
import com.market.openmarket.dto.CustomerSignUpResponseDto;
import com.market.openmarket.entity.Customer;
import com.market.openmarket.exception.EmailExistException;
import com.market.openmarket.exception.NicknameExistException;
import com.market.openmarket.exception.PhoneExistException;
import com.market.openmarket.repository.CustomerRepository;
import com.market.openmarket.util.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUnitTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

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
        when(customerRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(customerRepository.existsByNickname(requestDto.getNickname())).thenReturn(false);
        when(customerRepository.existsByPhone(requestDto.getPhone())).thenReturn(false);

        Customer customer = Customer.builder()
                .id(1)
                .email(requestDto.getEmail())
                .pwd(passwordEncoder.hashPassword(requestDto.getPwd()))
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .nickname(requestDto.getNickname())
                .address(requestDto.getAddress())
                .build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerSignUpResponseDto responseDto = customerService.signUp(requestDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(responseDto.getNickname()).isEqualTo(requestDto.getNickname());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void signUp_duplicated_email() {
        when(customerRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(EmailExistException.class, () -> {
            customerService.signUp(requestDto);
        });
    }

    @Test
    void signUp_duplicated_nickname() {
        when(customerRepository.existsByNickname(requestDto.getNickname())).thenReturn(true);

        assertThrows(NicknameExistException.class, () -> {
            customerService.signUp(requestDto);
        });
    }

    @Test
    void signUp_duplicated_phone() {
        when(customerRepository.existsByPhone(requestDto.getPhone())).thenReturn(true);

        assertThrows(PhoneExistException.class, () -> {
            customerService.signUp(requestDto);
        });
    }
}
