package com.camping.admin.repository;

import com.camping.admin.domain.entity.RentalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRecordRepository extends JpaRepository<RentalRecord, Long> {

    @Query("SELECT rr FROM RentalRecord rr WHERE rr.createdAt >= :startDateTime AND rr.createdAt < :endDateTime")
    List<RentalRecord> findByCreatedAtDate(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT rr FROM RentalRecord rr WHERE rr.createdAt >= :startDateTime AND rr.createdAt < :endDateTime")
    List<RentalRecord> findByCreatedAtDateBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}
