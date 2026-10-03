package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.DoctorResponse;
import com.example.springbootdemo.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bac si (Doctor)")
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAll(
            @RequestParam(required = false) String specialty) {
        return ResponseEntity.ok(doctorService.getAll(specialty));
    }
}