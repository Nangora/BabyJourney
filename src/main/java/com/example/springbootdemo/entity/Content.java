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
    private String type; // MUSIC / STORY / EXERCISE / ARTICLE

    @Column(name = "week_from", nullable = false)
    private Integer weekFrom;

    @Column(name = "week_to", nullable = false)
    private Integer weekTo;

    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}