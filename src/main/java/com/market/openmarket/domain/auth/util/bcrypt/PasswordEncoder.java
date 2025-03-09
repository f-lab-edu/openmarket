package com.market.openmarket.domain.auth.util.bcrypt;

public interface PasswordEncoder {

    String hash(String rawPassword);

    boolean checkPwd(String rawPassword, String hashedPassword);
}
