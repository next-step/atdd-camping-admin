package com.camping.admin.domain.entity;


import com.camping.admin.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    private ReservationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ReservationStatus newStatus;

    private LocalDateTime createdAt;

    public ReservationStatusHistory(Reservation reservation, ReservationStatus oldStatus, ReservationStatus newStatus) {
        this.reservation = reservation;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.createdAt = LocalDateTime.now();
    }
}
