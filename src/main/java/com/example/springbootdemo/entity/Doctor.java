package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(length = 150)
    private String specialty;

    @Column(name = "clinic_name", length = 200)
    private String clinicName;

    @Column(name = "clinic_address", length = 300)
    private String clinicAddress;

    @Column(name = "price_per_session", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerSession;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}