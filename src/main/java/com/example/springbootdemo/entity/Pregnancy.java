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
    private LocalDate dueDate; // LocalDate (khong phai LocalDateTime) vi cot DB la DATE, chi can ngay

    @Column(name = "last_period_date")
    private LocalDate lastPeriodDate;

    @Column(name = "baby_nickname", length = 100)
    private String babyNickname;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}