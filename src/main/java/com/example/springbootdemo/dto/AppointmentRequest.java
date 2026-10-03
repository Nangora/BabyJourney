package com.example.springbootdemo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {
    private Long doctorId;
    private LocalDateTime appointmentTime;
    private String notes;
}