package com.market.openmarket.repository;

import com.market.openmarket.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByNickname(String email);
    Optional<Customer> findByPhone(String email);
}
