package com.example.springbootdemo.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private String clinicName;
    private LocalDateTime appointmentTime;
    private String status;
    private String notes;
    private String resultNotes;
    private LocalDateTime createdAt;
}