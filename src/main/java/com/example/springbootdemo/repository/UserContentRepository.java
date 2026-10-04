// UserContentRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.UserContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserContentRepository extends JpaRepository<UserContent, Long> {

    Optional<UserContent> findByUser_IdAndContent_Id(Long userId, Long contentId);

    List<UserContent> findByUser_IdAndContent_IdIn(Long userId, Collection<Long> contentIds);

    // Số bài học của tuần này mà người dùng đã bắt đầu hoặc hoàn thành
    @Query("""
            select count(uc) from UserContent uc
            where uc.user.id = :userId and uc.content.kind = 'LESSON'
              and uc.content.weekFrom <= :week and uc.content.weekTo >= :week
              and uc.status in ('IN_PROGRESS', 'COMPLETED')
            """)
    long countExplored(@Param("userId") Long userId, @Param("week") int week);
}