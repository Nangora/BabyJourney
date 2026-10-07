package com.example.springbootdemo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private boolean emailVerified;
    private int currentStreak;
    private int longestStreak;
    private LocalDateTime createdAt;
}
