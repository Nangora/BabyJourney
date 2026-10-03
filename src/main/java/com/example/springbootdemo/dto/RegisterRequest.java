package com.example.springbootdemo.dto;

import lombok.Getter;
import lombok.Setter;

// DTO rieng cho du lieu dau vao khi dang ky, KHONG dung truc tiep Entity User
// vi User co nhieu truong (passwordHash, role, createdAt...) ma client khong duoc gui len
@Getter
@Setter
public class RegisterRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password; // mat khau THO, se duoc ma hoa (BCrypt) ben trong AuthService
}