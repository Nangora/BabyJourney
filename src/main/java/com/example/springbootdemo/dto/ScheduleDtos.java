// ScheduleDtos.java
package com.example.springbootdemo.dto;

import java.time.LocalDateTime;

public final class ScheduleDtos {

    private ScheduleDtos() {}

    public record ReminderRequest(String title, String note, LocalDateTime remindAt) {}

    // type: APPOINTMENT / REMINDER / MILESTONE
    public record ScheduleItem(String id, String type, String title, String subtitle, LocalDateTime at, boolean allDay) {}
}