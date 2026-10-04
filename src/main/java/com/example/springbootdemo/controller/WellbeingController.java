// WellbeingController.java
package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.WellbeingDtos.*;
import com.example.springbootdemo.service.WellbeingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tam trang & Nhat ky (Wellbeing)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WellbeingController {

    private final WellbeingService wellbeingService;

    // 200 + tâm trạng, hoặc 204 nếu hôm nay chưa chọn
    @GetMapping("/mood/today")
    public ResponseEntity<MoodView> todayMood(@AuthenticationPrincipal Long userId) {
        return wellbeingService.todayMood(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/mood/today")
    public MoodView setTodayMood(@AuthenticationPrincipal Long userId, @RequestBody MoodRequest request) {
        return wellbeingService.setTodayMood(userId, request.mood());
    }

    @GetMapping("/journal")
    public List<JournalView> list(@AuthenticationPrincipal Long userId,
                                  @RequestParam(required = false) String mood,
                                  @RequestParam(required = false) Integer limit) {
        return wellbeingService.listJournal(userId, mood, limit);
    }

    @PostMapping("/journal")
    public JournalView add(@AuthenticationPrincipal Long userId, @RequestBody JournalRequest request) {
        return wellbeingService.addJournal(userId, request);
    }

    @GetMapping("/journal/{id}")
    public JournalView get(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return wellbeingService.getJournal(userId, id);
    }

    @DeleteMapping("/journal/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        wellbeingService.deleteJournal(userId, id);
        return ResponseEntity.noContent().build();
    }
}