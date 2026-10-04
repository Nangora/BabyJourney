// ReminderRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUser_IdAndRemindAtBetween(Long userId, LocalDateTime from, LocalDateTime to);

    Optional<Reminder> findByIdAndUser_Id(Long id, Long userId);
}