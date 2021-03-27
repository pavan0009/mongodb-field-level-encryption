package com.micro.secureapp.crypt;

public class CryptOperationException extends RuntimeException {
    public CryptOperationException(String s, Throwable e) {
        super(s, e);
    }
}
