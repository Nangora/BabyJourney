// MoodEntryRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    Optional<MoodEntry> findByUser_IdAndEntryDate(Long userId, LocalDate date);
    List<MoodEntry> findByUser_IdOrderByEntryDateDesc(Long userId, Pageable pageable);
    List<MoodEntry> findByUser_IdAndEntryDateBetweenOrderByEntryDateDesc(Long userId, LocalDate from, LocalDate to);
}