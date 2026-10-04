// ContentDtos.java
package com.example.springbootdemo.dto;

import java.time.LocalDate;
import java.util.List;

public final class ContentDtos {

    private ContentDtos() {}

    // body và mediaUrl chỉ có khi lấy chi tiết (null khi lấy danh sách)
    public record ContentView(Long id, String kind, String category, String format, String title,
                              String description, Integer durationMin, Integer weekFrom, Integer weekTo,
                              String thumbnailUrl, List<String> tags,
                              String status, int progressPercent, boolean saved,
                              String body, String mediaUrl) {}

    public record StateRequest(Boolean saved, Integer progressPercent) {}

    public record WeekProgress(int week, int lessonsTotal, int lessonsExplored, int practiceDays,
                               int practiceDaysTotal, int journalEntries) {}

    public record PlanItem(String label, Long contentId, String title, String meta, boolean done) {}

    public record TodayView(LocalDate date, List<PlanItem> items) {}
}