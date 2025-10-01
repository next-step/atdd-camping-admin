package com.camping.admin.repository;

import com.camping.admin.domain.entity.SalesRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {

    @Query("SELECT sr FROM SalesRecord sr WHERE DATE(sr.createdAt) = :date")
    List<SalesRecord> findByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT sr FROM SalesRecord sr WHERE DATE(sr.createdAt) BETWEEN :fromDate AND :toDate")
    List<SalesRecord> findByCreatedAtDateBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT sr FROM SalesRecord sr ORDER BY sr.createdAt DESC")
    List<SalesRecord> findRecentSales(Pageable pageable);
}