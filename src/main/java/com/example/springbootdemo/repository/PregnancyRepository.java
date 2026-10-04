package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Pregnancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PregnancyRepository extends JpaRepository<Pregnancy, Long> {
    Optional<Pregnancy> findFirstByUser_IdOrderByIdDesc(Long userId);
}