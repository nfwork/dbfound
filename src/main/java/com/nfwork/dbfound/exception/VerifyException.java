package com.nfwork.dbfound.exception;

public class VerifyException extends CollisionException {
    public VerifyException(String message, String code) {
        super(message, code);
    }
}
