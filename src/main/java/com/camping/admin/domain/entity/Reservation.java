package com.camping.admin.domain.entity;

import com.camping.admin.domain.constants.BusinessConstants;
import com.camping.admin.domain.value.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    private LocalDate reservationDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campsite_id", nullable = false)
    private Campsite campsite;
    
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(length = 6)
    private String confirmationCode;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReservationStatus.CONFIRMED;
        }
    }
    
    public Reservation(String customerName, LocalDate startDate, LocalDate endDate, Campsite campsite) {
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.campsite = campsite;
    }

    public void updateStatus(String newStatus) {
        if (newStatus != null && !newStatus.isBlank()) {
            this.status = ReservationStatus.fromString(newStatus);
        }
    }

    public void updateStatus(ReservationStatus newStatus) {
        if (newStatus != null) {
            this.status = newStatus;
        }
    }

    public BigDecimal calculateRevenue() {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(this.startDate, this.endDate);
        if (nights < BusinessConstants.MINIMUM_NIGHTS) {
            nights = BusinessConstants.MINIMUM_NIGHTS;
        }
        return new BigDecimal(nights).multiply(BusinessConstants.RESERVATION_DAILY_RATE);
    }

    public boolean isActive() {
        return this.status != null && this.status.isActive();
    }

    public boolean isInDateRange(java.time.LocalDate from, java.time.LocalDate to) {
        if (this.reservationDate == null) return false;
        return (this.reservationDate.isEqual(from) || this.reservationDate.isAfter(from)) &&
               (this.reservationDate.isEqual(to) || this.reservationDate.isBefore(to));
    }
}