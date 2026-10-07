// JournalEntryRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.JournalEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByUser_IdAndDeletedFalseOrderByEntryDateDescIdDesc(Long userId, Pageable pageable);

    List<JournalEntry> findByUser_IdAndMoodAndDeletedFalseOrderByEntryDateDescIdDesc(Long userId, String mood, Pageable pageable);

    Optional<JournalEntry> findByIdAndUser_IdAndDeletedFalse(Long id, Long userId);

    long countByUser_IdAndDeletedFalseAndEntryDateBetween(Long userId, LocalDate from, LocalDate to);

    boolean existsByUser_IdAndEntryDateAndDeletedFalse(Long userId, LocalDate date);

    @Deprecated
    List<JournalEntry> findByUser_IdOrderByEntryDateDescIdDesc(Long userId, Pageable pageable);

    @Deprecated
    Optional<JournalEntry> findByIdAndUser_Id(Long id, Long userId);
}