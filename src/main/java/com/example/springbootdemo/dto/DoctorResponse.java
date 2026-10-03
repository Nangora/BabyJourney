package com.example.springbootdemo.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {
    private Long id;
    private String fullName;
    private String specialty;
    private String clinicName;
    private String clinicAddress;
    private BigDecimal pricePerSession;
}