// PregnancyRequest.java
package com.example.springbootdemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PregnancyRequest {
    private LocalDate dueDate;
    private List<String> goals;
    private Integer dailyMinutes;
    private List<String> learningStyles;
    private Boolean emailReminder;
}