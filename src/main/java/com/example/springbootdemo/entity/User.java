package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    // Bo "insertable = false": gio Java tu set gia tri nay truoc khi luu,
    // khong con dua vao SQL Server DEFAULT GETDATE() nua.
    // Van giu "updatable = false" de khong ai vo tinh sua ngay tao sau nay.
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Chay TU DONG ngay truoc khi Hibernate INSERT dong moi xuong DB.
    // Tu dien ngay gio hien tai vao createdAt - khong phu thuoc cu phap
    // rieng cua tung ban Hibernate nhu @Generated tung bi loi.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}