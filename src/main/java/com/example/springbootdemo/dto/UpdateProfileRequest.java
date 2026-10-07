package com.example.springbootdemo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String avatarUrl;
    private LocalDate dateOfBirth;
}