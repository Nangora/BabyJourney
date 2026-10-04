package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUser_IdOrderByAppointmentTimeDesc(Long userId);

    // Dùng để check trùng giờ: bác sĩ này đã có lịch PENDING/CONFIRMED đúng thời điểm chưa
    boolean existsByDoctor_IdAndAppointmentTimeAndStatusIn(
            Long doctorId, LocalDateTime appointmentTime, List<String> statuses);

    List<Appointment> findByUser_IdAndAppointmentTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);
}