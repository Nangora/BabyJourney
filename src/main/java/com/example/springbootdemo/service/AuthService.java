package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.*;
import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.repository.UserRepository;
import com.example.springbootdemo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // THEM MOI: dung de tao token sau khi login

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email da duoc su dung");
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    // THAY DOI: tra ve AuthResponse (co token) thay vi chi UserResponse
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoac mat khau khong dung"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Email hoac mat khau khong dung");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, toUserResponse(user));
    }

    // THEM MOI: dung cho endpoint /api/auth/me, lay thong tin user tu userId trong token
    public UserResponse getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi dung"));
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(), user.getFullName(), user.getEmail(),
                user.getPhone(), user.getRole(), user.getCreatedAt());
    }
}