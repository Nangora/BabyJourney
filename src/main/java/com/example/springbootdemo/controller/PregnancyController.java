package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.PregnancyRequest;
import com.example.springbootdemo.dto.PregnancyResponse;
import com.example.springbootdemo.service.PregnancyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ho so thai ky (Pregnancy)")
@RestController
@RequestMapping("/api/pregnancy")
@RequiredArgsConstructor
public class PregnancyController {

    private final PregnancyService pregnancyService;

    // 200 + hồ sơ, hoặc 204 No Content nếu người dùng chưa tạo hồ sơ
    @GetMapping("/me")
    public ResponseEntity<PregnancyResponse> me(@AuthenticationPrincipal Long userId) {
        return pregnancyService.getMine(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/me")
    public ResponseEntity<PregnancyResponse> save(
            @AuthenticationPrincipal Long userId,
            @RequestBody PregnancyRequest request) {
        return ResponseEntity.ok(pregnancyService.save(userId, request));
    }
}