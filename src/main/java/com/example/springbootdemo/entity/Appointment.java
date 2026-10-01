package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    @Column(nullable = false, length = 20)
    private String status; // PENDING / CONFIRMED / CANCELLED

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}