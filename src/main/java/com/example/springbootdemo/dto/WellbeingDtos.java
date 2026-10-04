// WellbeingDtos.java
package com.example.springbootdemo.dto;

import java.time.LocalDate;

public final class WellbeingDtos {

    private WellbeingDtos() {}

    public record MoodRequest(String mood) {}

    public record MoodView(LocalDate date, String mood) {}

    public record JournalRequest(String body, String mood, LocalDate entryDate) {}

    public record JournalView(Long id, LocalDate entryDate, Integer weekNumber, String title, String body, String mood) {}
}