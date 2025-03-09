package com.market.openmarket.util.jwt;

import com.market.openmarket.auth.JwtToken;
import com.market.openmarket.entity.User;

public interface JwtProvider {

    JwtToken generateAccessToken(User user);

    JwtToken generateRefreshToken(User user);
}
