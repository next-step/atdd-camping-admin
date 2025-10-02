package com.camping.admin.repository;

import com.camping.admin.domain.entity.SalesRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {

    @Query("SELECT sr FROM SalesRecord sr WHERE sr.createdAt >= :startDateTime AND sr.createdAt < :endDateTime")
    List<SalesRecord> findByCreatedAtDate(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT sr FROM SalesRecord sr WHERE sr.createdAt >= :startDateTime AND sr.createdAt < :endDateTime")
    List<SalesRecord> findByCreatedAtDateBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT sr FROM SalesRecord sr ORDER BY sr.createdAt DESC")
    List<SalesRecord> findRecentSales(Pageable pageable);
}