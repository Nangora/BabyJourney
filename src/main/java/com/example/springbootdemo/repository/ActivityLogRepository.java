// ActivityLogRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    boolean existsByUser_IdAndContent_IdAndCompletedOn(Long userId, Long contentId, LocalDate date);

    @Query("select count(distinct a.completedOn) from ActivityLog a "
            + "where a.user.id = :userId and a.completedOn between :from and :to")
    long countPracticeDays(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    long countByUser_Id(Long userId);

    long countByUser_IdAndCompletedOnBetween(Long userId, LocalDate from, LocalDate to);
}