package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.AppointmentRequest;
import com.example.springbootdemo.dto.AppointmentResponse;
import com.example.springbootdemo.entity.Appointment;
import com.example.springbootdemo.entity.Doctor;
import com.example.springbootdemo.entity.User;
import com.example.springbootdemo.repository.AppointmentRepository;
import com.example.springbootdemo.repository.DoctorRepository;
import com.example.springbootdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final List<String> ACTIVE_STATUSES = List.of("PENDING", "CONFIRMED");

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AppointmentResponse create(Long userId, AppointmentRequest request) {
        if (request.getDoctorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "doctorId khong duoc de trong");
        }
        if (request.getAppointmentTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "appointmentTime khong duoc de trong");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User khong ton tai"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Khong tim thay bac si"));

        boolean trung = appointmentRepository.existsByDoctor_IdAndAppointmentTimeAndStatusIn(
                doctor.getId(), request.getAppointmentTime(), ACTIVE_STATUSES);
        if (trung) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Bac si da co lich vao khung gio nay, vui long chon gio khac");
        }

        Appointment appointment = Appointment.builder()
                .user(user)
                .doctor(doctor)
                .appointmentTime(request.getAppointmentTime())
                .status("PENDING")
                .notes(request.getNotes())
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getMyAppointments(Long userId) {
        return appointmentRepository.findByUser_IdOrderByAppointmentTimeDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getFullName())
                .clinicName(a.getDoctor().getClinicName())
                .appointmentTime(a.getAppointmentTime())
                .status(a.getStatus())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }
}