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
    private String status;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "appointment_type", length = 50)
    private String appointmentType;

    @Column(name = "result_notes", columnDefinition = "NVARCHAR(MAX)")
    private String resultNotes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "remind_before_minutes")
    private Integer remindBeforeMinutes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
