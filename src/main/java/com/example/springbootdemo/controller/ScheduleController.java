// ScheduleController.java
package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.ScheduleDtos.ReminderRequest;
import com.example.springbootdemo.dto.ScheduleDtos.ScheduleItem;
import com.example.springbootdemo.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Lich (Schedule)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // Không truyền year/month thì lấy tháng hiện tại
    @GetMapping("/schedule")
    public List<ScheduleItem> month(@AuthenticationPrincipal Long userId,
                                    @RequestParam(required = false) Integer year,
                                    @RequestParam(required = false) Integer month) {
        return scheduleService.month(userId, year, month);
    }

    @GetMapping("/schedule/upcoming")
    public List<ScheduleItem> upcoming(@AuthenticationPrincipal Long userId,
                                       @RequestParam(required = false) Integer limit) {
        return scheduleService.upcoming(userId, limit);
    }

    @PostMapping("/reminders")
    public ScheduleItem addReminder(@AuthenticationPrincipal Long userId, @RequestBody ReminderRequest request) {
        return scheduleService.addReminder(userId, request);
    }

    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<Void> deleteReminder(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        scheduleService.deleteReminder(userId, id);
        return ResponseEntity.noContent().build();
    }
}