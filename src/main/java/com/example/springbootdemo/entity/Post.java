package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content; // noi dung bai dang - khong lien quan gi den class Content o tren

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}