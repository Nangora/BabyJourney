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

    @Column(nullable = false, length = 20)
    private String type;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String kind = "LESSON";

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

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "source_references", columnDefinition = "NVARCHAR(MAX)")
    private String sourceReferences;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
