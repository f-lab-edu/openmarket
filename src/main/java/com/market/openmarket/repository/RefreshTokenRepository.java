package com.market.openmarket.repository;

import com.market.openmarket.entity.RefreshToken;
import com.market.openmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long id);
}
