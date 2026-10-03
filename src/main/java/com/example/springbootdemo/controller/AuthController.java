package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.*;
import com.example.springbootdemo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Xac thuc (Auth)") // Ten nhom hien thi tren trang Swagger
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Endpoint nay dung de TEST xem JWT co hoat dong dung khong,
    // va sau nay Angular dung de lay lai thong tin user khi load lai trang (F5)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(authService.getById(userId));
    }
}