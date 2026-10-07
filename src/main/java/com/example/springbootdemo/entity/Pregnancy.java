package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pregnancies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pregnancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "last_period_date")
    private LocalDate lastPeriodDate;

    @Column(name = "baby_nickname", length = 100)
    private String babyNickname;

    @Column(length = 200)
    private String goals;

    @Column(name = "daily_minutes")
    private Integer dailyMinutes;

    @Column(name = "learning_styles", length = 200)
    private String learningStyles;

    @Builder.Default
    @Column(name = "email_reminder", nullable = false)
    private boolean emailReminder = false;

    @Builder.Default
    @Column(name = "number_of_babies", nullable = false)
    private int numberOfBabies = 1;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "archived_reason", length = 50)
    private String archivedReason;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
