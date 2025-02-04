package com.market.openmarket.exception;

public class EmailExistException extends RuntimeException {

    public EmailExistException() {
        super("이미 존재하는 이메일입니다.");
    }
}
