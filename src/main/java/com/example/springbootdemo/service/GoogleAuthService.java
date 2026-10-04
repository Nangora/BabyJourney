package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.AuthResponse;
import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Value("${app.google.client-id}")
    private String clientId;

    private final RestClient rest = RestClient.create();

    @Transactional
    public AuthResponse login(String idToken) {
        Map<String, Object> info = verify(idToken);

        String googleId = String.valueOf(info.get("sub"));
        String email = AuthService.normalizeEmail(String.valueOf(info.get("email")));
        String name = info.get("name") == null ? email.substring(0, email.indexOf('@')) : String.valueOf(info.get("name"));

        // 1) Đã liên kết Google  2) Đã có tài khoản cùng email -> liên kết  3) Chưa có -> tạo mới
        User user = userRepository.findByGoogleId(googleId).orElseGet(() ->
                userRepository.findByEmail(email).map(existing -> {
                    existing.setGoogleId(googleId);
                    return userRepository.save(existing);
                }).orElseGet(() -> userRepository.save(User.builder()
                        .fullName(name.length() > 150 ? name.substring(0, 150) : name)
                        .email(email)
                        // Tài khoản Google không có mật khẩu: lưu hash của chuỗi ngẫu nhiên
                        .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .role("USER")
                        .googleId(googleId)
                        .build())));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không thể đăng nhập bằng Google");
        }
        return authService.issue(user);
    }

    private Map<String, Object> verify(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu thông tin xác thực Google");
        }
        try {
            Map<String, Object> info = rest.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={t}", idToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            boolean ok = info != null
                    && clientId.equals(info.get("aud"))
                    && "true".equals(String.valueOf(info.get("email_verified")))
                    && info.get("sub") != null
                    && info.get("email") != null;
            if (!ok) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Xác thực Google không hợp lệ");
            return info;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Xác thực Google không hợp lệ");
        }
    }
}