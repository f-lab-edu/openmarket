package com.market.openmarket.exception;

public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("이미 등록된 전화번호입니다.");
    }
}
