package com.example.springbootdemo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    // Định dạng: ARTICLE / VISUAL_GUIDE / AUDIO / CHECKLIST / PROMPT / READING
    @Column(nullable = false, length = 20)
    private String type;

    // LESSON (Kiến thức) hoặc ACTIVITY (Hoạt động)
    @Column(nullable = false, length = 20)
    private String kind;

    // Bài học: BABY_DEVELOPMENT / YOUR_BODY / EVERYDAY_WELLBEING / PREGNANCY_CARE / CONNECTION
    // Hoạt động: MUSIC / READING / TALKING_TO_BABY / RELAXATION / MEDITATION
    @Column(length = 30)
    private String category;

    @Column(name = "week_from", nullable = false)
    private Integer weekFrom;

    @Column(name = "week_to", nullable = false)
    private Integer weekTo;

    @Column(name = "duration_min")
    private Integer durationMin;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String body;

    @Column(length = 200)
    private String tags;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}