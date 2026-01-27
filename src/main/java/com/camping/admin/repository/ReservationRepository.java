package com.camping.admin.repository;

import com.camping.admin.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByCampsiteId(Long campsiteId);

    @Query("SELECT r FROM Reservation r WHERE r.campsite.id = :campsiteId " +
           "AND r.status != 'CANCELLED' " +
           "AND (r.timing.stayPeriod.startDate < :endDate AND r.timing.stayPeriod.endDate > :startDate)")
    List<Reservation> findOverlappingReservations(
        @Param("campsiteId") Long campsiteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM Reservation r WHERE r.timing.reservationDate = :date")
    List<Reservation> findByReservationDate(@Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE r.timing.reservationDate >= :from AND r.timing.reservationDate <= :to")
    List<Reservation> findByReservationDateBetween(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}