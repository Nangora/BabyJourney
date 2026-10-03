package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUser_IdOrderByAppointmentTimeDesc(Long userId);

    // Dung de check trung gio: bac si nay da co lich PENDING/CONFIRMED dung thoi diem chua
    boolean existsByDoctor_IdAndAppointmentTimeAndStatusIn(
            Long doctorId, LocalDateTime appointmentTime, List<String> statuses);
}