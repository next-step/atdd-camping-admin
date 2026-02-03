package com.camping.admin.repository;

import com.camping.admin.domain.entity.SalesRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {

    List<SalesRecord> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT sr FROM SalesRecord sr WHERE CAST(sr.createdAt AS DATE) = :date")
    List<SalesRecord> findByCreatedDate(@Param("date") LocalDate date);

    @Query("SELECT sr FROM SalesRecord sr WHERE CAST(sr.createdAt AS DATE) BETWEEN :from AND :to")
    List<SalesRecord> findByCreatedDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}