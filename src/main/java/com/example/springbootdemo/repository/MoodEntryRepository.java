// MoodEntryRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    Optional<MoodEntry> findByUser_IdAndEntryDate(Long userId, LocalDate date);
}