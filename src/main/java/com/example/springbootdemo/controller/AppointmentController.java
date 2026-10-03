package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.AppointmentRequest;
import com.example.springbootdemo.dto.AppointmentResponse;
import com.example.springbootdemo.service.AppointmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Dat lich kham (Appointment)")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(
            @AuthenticationPrincipal Long userId,
            @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.create(userId, request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(appointmentService.getMyAppointments(userId));
    }
}