package com.camping.admin.repository;

import com.camping.admin.domain.entity.RentalRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRecordRepository extends JpaRepository<RentalRecord, Long> {
    List<RentalRecord> findByReservationId(Long reservationId);

    List<RentalRecord> findByProductId(Long productId);

    List<RentalRecord> findByIsReturned(Boolean isReturned);

    @Query("SELECT rr FROM RentalRecord rr " +
           "LEFT JOIN rr.reservation r " +
           "WHERE rr.product.id = :productId " +
           "AND rr.isReturned = false")
    List<RentalRecord> findActiveRentalsByProductId(@Param("productId") Long productId);

    @Query("SELECT rr FROM RentalRecord rr WHERE rr.createdAt >= :start AND rr.createdAt < :end")
    List<RentalRecord> findByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(rr.product.price * rr.quantity), 0) FROM RentalRecord rr WHERE rr.createdAt >= :start AND rr.createdAt < :end")
    BigDecimal sumRevenueByCreatedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}