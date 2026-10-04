// UserContent.java
package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "NOT_STARTED";

    @Builder.Default
    @Column(name = "progress_percent", nullable = false)
    private int progressPercent = 0;

    @Builder.Default
    @Column(nullable = false)
    private boolean saved = false;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}