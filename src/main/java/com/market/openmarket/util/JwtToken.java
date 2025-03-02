package com.market.openmarket.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class JwtToken {

    private final String token;
    private final Date issuedAt;
    private final Date expiration;
}
