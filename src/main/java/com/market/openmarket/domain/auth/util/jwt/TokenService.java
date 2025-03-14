package com.market.openmarket.domain.auth.util.jwt;

public interface TokenService {

    String savePasswordResetToken(String email);

    void validatePasswordResetToken(String email, String token);

    void deletePasswordResetToken(String email);
}
