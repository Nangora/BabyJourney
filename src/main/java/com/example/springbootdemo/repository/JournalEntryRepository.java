// JournalEntryRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.JournalEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByUser_IdOrderByEntryDateDescIdDesc(Long userId, Pageable pageable);

    List<JournalEntry> findByUser_IdAndMoodOrderByEntryDateDescIdDesc(Long userId, String mood, Pageable pageable);

    Optional<JournalEntry> findByIdAndUser_Id(Long id, Long userId);

    long countByUser_IdAndEntryDateBetween(Long userId, LocalDate from, LocalDate to);

    boolean existsByUser_IdAndEntryDate(Long userId, LocalDate date);
}