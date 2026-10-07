package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.ResetPasswordRequest;
import com.example.springbootdemo.entity.PasswordResetToken;
import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.repository.PasswordResetTokenRepository;
import com.example.springbootdemo.repository.UserRepository;
import com.example.springbootdemo.security.PasswordPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final String INVALID_LINK = "Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;
    @Value("${app.password-reset.expiration-minutes}")
    private long expirationMinutes;
    @Value("${app.mail.from}")
    private String mailFrom;
    @Value("${app.invite.expiration-hours:72}")
    private long inviteExpirationHours;

    private final SecureRandom random = new SecureRandom();

    // ---------- 4. Quên mật khẩu ----------
    // Luôn trả về như nhau dù email có tồn tại hay không (controller trả cùng 1 thông báo)
    @Transactional
    public void requestReset(String rawEmail) {
        String email = AuthService.normalizeEmail(rawEmail);
        if (email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập email");
        }

        userRepository.findByEmail(email).filter(User::isActive).ifPresent(user -> {
            String token = createToken(user, expirationMinutes);
            sendMail(user.getEmail(), frontendUrl + "/reset-password?token=" + token);
        });
    }

    // ---------- Mời bác sĩ: gửi link tự đặt mật khẩu lần đầu (dùng lại trang /reset-password) ----------
    @Transactional
    public void sendDoctorInvite(User user) {
        String token = createToken(user, inviteExpirationHours * 60);
        sendInviteMail(user.getEmail(), user.getFullName(), frontendUrl + "/reset-password?token=" + token);
    }

    private String createToken(User user, long ttlMinutes) {
        tokenRepository.invalidateAllForUser(user.getId());

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        tokenRepository.save(PasswordResetToken.builder()
                .user(user)
                .tokenHash(sha256(token))
                .expiresAt(LocalDateTime.now().plusMinutes(ttlMinutes))
                .used(false)
                .build());
        return token;
    }

    // ---------- 5. Đặt lại mật khẩu ----------
    @Transactional
    public void resetPassword(ResetPasswordRequest r) {
        if (r.getToken() == null || r.getToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_LINK);
        }
        if (r.getNewPassword() == null || r.getConfirmPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập đầy đủ thông tin");
        }
        PasswordPolicy.validate(r.getNewPassword());
        if (!r.getNewPassword().equals(r.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu xác nhận không khớp");
        }

        PasswordResetToken t = tokenRepository.findByTokenHash(sha256(r.getToken()))
                .filter(x -> !x.isUsed() && x.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_LINK));

        User user = t.getUser();
        if (!user.isActive()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_LINK);

        user.setPasswordHash(passwordEncoder.encode(r.getNewPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1); // đăng xuất mọi phiên cũ
        userRepository.save(user);

        t.setUsed(true);
        tokenRepository.save(t);
    }

    private void sendInviteMail(String to, String fullName, String link) {
        // Log link để test local khi chưa cấu hình SMTP. XÓA dòng này khi lên production.
        log.info("[DEV] Link đặt mật khẩu cho bác sĩ {}: {}", to, link);

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(mailFrom);
            msg.setTo(to);
            msg.setSubject("BabyJourney - Kích hoạt tài khoản bác sĩ");
            msg.setText("Xin chào " + fullName + ",\n\n"
                    + "Tài khoản bác sĩ của bạn trên BabyJourney đã được tạo. "
                    + "Nhấn vào liên kết sau để đặt mật khẩu (có hiệu lực " + inviteExpirationHours + " giờ):\n"
                    + link + "\n\nNếu bạn không mong đợi email này, hãy bỏ qua.");
            sender.send(msg);
        } catch (Exception e) {
            log.warn("Không gửi được email mời bác sĩ: {}", e.getMessage());
        }
    }

    private void sendMail(String to, String link) {
        // Log link để test local khi chưa cấu hình SMTP. XÓA dòng này khi lên production.
        log.info("[DEV] Link đặt lại mật khẩu cho {}: {}", to, link);

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(mailFrom);
            msg.setTo(to);
            msg.setSubject("BabyJourney - Đặt lại mật khẩu");
            msg.setText("Bạn vừa yêu cầu đặt lại mật khẩu.\n\n"
                    + "Nhấn vào liên kết sau (có hiệu lực " + expirationMinutes + " phút):\n" + link
                    + "\n\nNếu bạn không yêu cầu, hãy bỏ qua email này.");
            sender.send(msg);
        } catch (Exception e) {
            log.warn("Không gửi được email đặt lại mật khẩu: {}", e.getMessage());
        }
    }

    private static String sha256(String s) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}