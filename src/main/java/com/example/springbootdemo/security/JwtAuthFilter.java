package com.example.springbootdemo.security;

import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Mỗi request: kiểm tra token, tài khoản còn tồn tại + đang active, và token_version khớp.
 * Hợp lệ -> gắn userId + quyền (ROLE_USER / ROLE_DOCTOR / ROLE_ADMIN) vào SecurityContext.
 * Không hợp lệ -> không gắn gì, SecurityConfig sẽ trả 401 cho endpoint được bảo vệ.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.parse(authHeader.substring(7));
                Long userId = Long.valueOf(claims.getSubject());
                int version = jwtService.extractVersion(claims);

                userRepository.findById(userId)
                        .filter(User::isActive)
                        .filter(u -> u.getTokenVersion() == version)
                        .ifPresent(u -> SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        u.getId(), null,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole())))));
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}