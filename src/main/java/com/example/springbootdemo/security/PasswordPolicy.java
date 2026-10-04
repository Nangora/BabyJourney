package com.example.springbootdemo.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class PasswordPolicy {

    private PasswordPolicy() {}

    // Tối thiểu 8 ký tự (tối đa 72 vì giới hạn của BCrypt), có ít nhất 1 chữ cái và 1 chữ số
    public static void validate(String password) {
        boolean ok = password != null
                && password.length() >= 8
                && password.length() <= 72
                && password.matches(".*[A-Za-z].*")
                && password.matches(".*\\d.*");
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mật khẩu phải từ 8 đến 72 ký tự, gồm ít nhất 1 chữ cái và 1 chữ số");
        }
    }
}