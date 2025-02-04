package com.market.openmarket.exception;

public class NicknameExistException extends RuntimeException {

    public NicknameExistException() {
        super("이미 존재하는 닉네임입니다.");
    }
}
