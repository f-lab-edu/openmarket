package com.market.openmarket.domain.auth.util.jwt;

import com.market.openmarket.domain.auth.JwtToken;
import com.market.openmarket.domain.user.entity.User;

public interface JwtProvider {

    JwtToken generateAccessToken(User user);

    JwtToken generateRefreshToken(User user);
}
