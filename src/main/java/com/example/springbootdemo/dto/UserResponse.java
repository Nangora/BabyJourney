package com.example.springbootdemo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

// DTO tra ve cho client sau khi dang ky/dang nhap/lay thong tin user.
// Co y LOAI BO passwordHash khoi response - khong bao gio duoc tra mat khau
// (du da hash) ve cho client, tranh lo thong tin nhay cam
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private LocalDateTime createdAt;
}