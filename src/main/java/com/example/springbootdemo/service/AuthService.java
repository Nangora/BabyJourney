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

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\s-]{9,15}$");
    private static final String BAD_CREDENTIALS = "Email hoặc mật khẩu không đúng";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    public static boolean isValidEmail(String normalizedEmail) {
        return normalizedEmail != null && normalizedEmail.length() <= 150 && EMAIL.matcher(normalizedEmail).matches();
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    // ---------- 1. Đăng ký ----------
    @Transactional
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
                .emailVerified(false)
                .agreedTerms(r.isAgreedTerms())
                .build());
        return toUserResponse(saved);
    }

    // ---------- 2.1 Đăng nhập with lockout ----------
    @Transactional
    public AuthResponse login(LoginRequest r) {
        String email = normalizeEmail(r.getEmail());
        if (email.isEmpty() || isBlank(r.getPassword())) throw bad("Vui lòng nhập email và mật khẩu");

        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Tài khoản đã bị khóa tạm thời. Vui lòng thử lại sau.");
        }

        if (!passwordEncoder.matches(r.getPassword(), user.getPasswordHash())) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
                user.setLoginAttempts(0);
            }
            userRepository.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, BAD_CREDENTIALS);
        }

        user.setLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        return issue(user);
    }

    public AuthResponse issue(User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getTokenVersion());
        return new AuthResponse(token, toUserResponse(user));
    }

    // ---------- 3. Đăng xuất ----------
    @Transactional
    public void logout(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setTokenVersion(u.getTokenVersion() + 1);
            userRepository.save(u);
        });
    }

    // ---------- 7. Đổi mật khẩu ----------
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
        if (r.getAvatarUrl() != null) user.setAvatarUrl(r.getAvatarUrl().trim());
        if (r.getDateOfBirth() != null) user.setDateOfBirth(r.getDateOfBirth());
        return toUserResponse(userRepository.save(user));
    }

    // ---------- Admin: user management ----------
    public User getUserEntity(Long userId) {
        return getUser(userId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .filter(User::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ"));
    }

    static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(), user.getFullName(), user.getEmail(),
                user.getPhone(), user.getRole(), user.getAvatarUrl(),
                user.getDateOfBirth(), user.isEmailVerified(),
                user.getCurrentStreak(), user.getLongestStreak(),
                user.getCreatedAt());
    }
}