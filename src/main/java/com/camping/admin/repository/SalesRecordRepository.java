package com.camping.admin.repository;

import com.camping.admin.domain.entity.SalesRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {

    @Query("SELECT s FROM SalesRecord s WHERE s.createdAt >= :start AND s.createdAt < :end")
    List<SalesRecord> findByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(s.totalAmount.value), 0) FROM SalesRecord s WHERE s.createdAt >= :start AND s.createdAt < :end")
    BigDecimal sumTotalPriceByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT s FROM SalesRecord s order by s.createdAt desc limit :limit")
    List<SalesRecord> findRecentSalesRecords(int limit);
}