package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.WellbeingDtos.*;
import com.example.springbootdemo.service.WellbeingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Tam trang & Nhat ky (Wellbeing)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WellbeingController {

    private final WellbeingService wellbeingService;

    @GetMapping("/mood/today")
    public ResponseEntity<MoodView> todayMood(@AuthenticationPrincipal Long userId) {
        return wellbeingService.todayMood(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/mood/today")
    public MoodView setTodayMood(@AuthenticationPrincipal Long userId, @RequestBody MoodRequest request) {
        return wellbeingService.setTodayMood(userId, request.mood(), request.note());
    }

    @GetMapping("/mood/history")
    public List<MoodView> moodHistory(@AuthenticationPrincipal Long userId,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return wellbeingService.moodHistory(userId, from, to);
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

    @PutMapping("/journal/{id}")
    public JournalView update(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                              @RequestBody JournalRequest request) {
        return wellbeingService.updateJournal(userId, id, request);
    }

    @DeleteMapping("/journal/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        wellbeingService.deleteJournal(userId, id);
        return ResponseEntity.noContent().build();
    }
}
