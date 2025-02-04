package com.market.openmarket.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    public String hashPassword(String pwd) {
        return BCrypt.hashpw(pwd, BCrypt.gensalt());
    }
}
