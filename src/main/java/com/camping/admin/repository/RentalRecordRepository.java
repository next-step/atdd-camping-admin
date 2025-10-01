package com.camping.admin.repository;

import com.camping.admin.domain.entity.RentalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRecordRepository extends JpaRepository<RentalRecord, Long> {

    @Query("SELECT rr FROM RentalRecord rr WHERE DATE(rr.createdAt) = :date")
    List<RentalRecord> findByCreatedAtDate(@Param("date") LocalDate date);

    @Query("SELECT rr FROM RentalRecord rr WHERE DATE(rr.createdAt) BETWEEN :fromDate AND :toDate")
    List<RentalRecord> findByCreatedAtDateBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
