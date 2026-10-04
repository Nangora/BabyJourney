package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.*;
import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.repository.UserRepository;
import com.example.springbootdemo.security.JwtService;
import com.example.springbootdemo.security.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\s-]{9,15}$");
    // Thông báo chung, không tiết lộ email có tồn tại / tài khoản bị khóa hay không
    private static final String BAD_CREDENTIALS = "Email hoặc mật khẩu không đúng";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    // ---------- 1. Đăng ký ----------
    public UserResponse register(RegisterRequest r) {
        String fullName = r.getFullName() == null ? "" : r.getFullName().trim();
        String email = normalizeEmail(r.getEmail());

        if (fullName.isEmpty() || email.isEmpty()
                || isBlank(r.getPassword()) || isBlank(r.getConfirmPassword())) {
            throw bad("Vui lòng nhập đầy đủ các thông tin bắt buộc");
        }
        if (fullName.length() > 150) throw bad("Họ tên quá dài");
        if (email.length() > 150 || !EMAIL.matcher(email).matches()) throw bad("Email không hợp lệ");
        PasswordPolicy.validate(r.getPassword());
        if (!r.getPassword().equals(r.getConfirmPassword())) throw bad("Mật khẩu xác nhận không khớp");
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này đã được sử dụng");
        }

        User saved = userRepository.save(User.builder()
                .fullName(fullName)
                .email(email)
                .phone(isBlank(r.getPhone()) ? null : r.getPhone().trim())
                .passwordHash(passwordEncoder.encode(r.getPassword()))
                .role("USER")
                .build());
        return toUserResponse(saved);
    }

    // ---------- 2.1 Đăng nhập ----------
    public AuthResponse login(LoginRequest r) {
        String email = normalizeEmail(r.getEmail());
        if (email.isEmpty() || isBlank(r.getPassword())) throw bad("Vui lòng nhập email và mật khẩu");

        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .filter(u -> passwordEncoder.matches(r.getPassword(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS));

        return issue(user);
    }

    public AuthResponse issue(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getTokenVersion());
        return new AuthResponse(token, toUserResponse(user));
    }

    // ---------- 3. Đăng xuất: vô hiệu hóa toàn bộ token đã cấp ----------
    @Transactional
    public void logout(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setTokenVersion(u.getTokenVersion() + 1);
            userRepository.save(u);
        });
    }

    // ---------- 7. Đổi mật khẩu (trả token mới để thiết bị hiện tại không bị văng) ----------
    @Transactional
    public AuthResponse changePassword(Long userId, ChangePasswordRequest r) {
        User user = getUser(userId);

        if (isBlank(r.getCurrentPassword()) || isBlank(r.getNewPassword()) || isBlank(r.getConfirmPassword())) {
            throw bad("Vui lòng nhập đầy đủ thông tin");
        }
        if (!passwordEncoder.matches(r.getCurrentPassword(), user.getPasswordHash())) {
            throw bad("Mật khẩu hiện tại không đúng");
        }
        PasswordPolicy.validate(r.getNewPassword());
        if (!r.getNewPassword().equals(r.getConfirmPassword())) throw bad("Mật khẩu xác nhận không khớp");
        if (r.getNewPassword().equals(r.getCurrentPassword())) {
            throw bad("Mật khẩu mới phải khác mật khẩu hiện tại");
        }

        user.setPasswordHash(passwordEncoder.encode(r.getNewPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        return issue(userRepository.save(user));
    }

    // ---------- 8. Thông tin tài khoản ----------
    public UserResponse getById(Long userId) {
        return toUserResponse(getUser(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest r) {
        User user = getUser(userId);
        String fullName = r.getFullName() == null ? "" : r.getFullName().trim();
        String phone = isBlank(r.getPhone()) ? null : r.getPhone().trim();

        if (fullName.isEmpty()) throw bad("Họ tên không được để trống");
        if (fullName.length() > 150) throw bad("Họ tên quá dài");
        if (phone != null && !PHONE.matcher(phone).matches()) throw bad("Số điện thoại không hợp lệ");

        user.setFullName(fullName);
        user.setPhone(phone);
        return toUserResponse(userRepository.save(user));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ"));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(),
                user.getPhone(), user.getRole(), user.getCreatedAt());
    }
}