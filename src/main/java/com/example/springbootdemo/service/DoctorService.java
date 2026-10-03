package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.DoctorResponse;
import com.example.springbootdemo.entity.Doctor;
import com.example.springbootdemo.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorResponse> getAll(String specialty) {
        List<Doctor> doctors = (specialty == null || specialty.isBlank())
                ? doctorRepository.findAll()
                : doctorRepository.findBySpecialtyContainingIgnoreCase(specialty);

        return doctors.stream().map(this::toResponse).toList();
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .specialty(doctor.getSpecialty())
                .clinicName(doctor.getClinicName())
                .clinicAddress(doctor.getClinicAddress())
                .pricePerSession(doctor.getPricePerSession())
                .build();
    }
}