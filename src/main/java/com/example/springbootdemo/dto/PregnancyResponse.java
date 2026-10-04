// PregnancyResponse.java
package com.example.springbootdemo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PregnancyResponse {
    private LocalDate dueDate;
    private List<String> goals;
    private Integer dailyMinutes;
    private List<String> learningStyles;
    private boolean emailReminder;
    private int currentWeek;
    private int trimester;
}