package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecialtyContainingIgnoreCase(String specialty);

    Optional<Doctor> findByUser_Id(Long userId);

    List<Doctor> findByUserIsNull();

    // Chỉ bác sĩ có tài khoản đang hoạt động mới nhận được lịch đặt
    List<Doctor> findByUser_ActiveTrue();

    List<Doctor> findBySpecialtyContainingIgnoreCaseAndUser_ActiveTrue(String specialty);

    boolean existsByIdAndUser_ActiveTrue(Long id);
}