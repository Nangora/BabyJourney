// ContentRepository.java
package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    // Dùng chuỗi rỗng / dải tuần 0-99 thay cho "không lọc" để tránh lỗi gán null trên SQL Server
    @Query("""
            select c from Content c
            where (:kind = '' or c.kind = :kind)
              and (:category = '' or c.category = :category)
              and c.weekFrom <= :wMax and c.weekTo >= :wMin
              and (:q = '' or lower(c.title) like lower(concat('%', :q, '%'))
                           or lower(c.description) like lower(concat('%', :q, '%')))
            order by c.weekFrom, c.id
            """)
    List<Content> search(@Param("kind") String kind,
                         @Param("category") String category,
                         @Param("wMin") int wMin,
                         @Param("wMax") int wMax,
                         @Param("q") String q);

    long countByKindAndWeekFromLessThanEqualAndWeekToGreaterThanEqual(String kind, int week1, int week2);
}