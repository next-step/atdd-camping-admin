package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    
    private String status;
    
    @Column(length = 6)
    private String confirmationCode;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "CONFIRMED";
        }
    }
    
    public Reservation(String customerName, LocalDate startDate, LocalDate endDate, Campsite campsite) {
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.campsite = campsite;
    }

    public BigDecimal calculateReservationRevenue() {
        long nights = ChronoUnit.DAYS.between(this.startDate, this.endDate);
        if (nights < 1) {
            nights = 1; // 최소 1박 처리
        }
        return new BigDecimal(nights).multiply(new BigDecimal("50000"));
    }

    public boolean isInDateRange(LocalDate from, LocalDate to) {
        if (this.reservationDate == null) {
            return false;
        }
        LocalDate date = this.reservationDate;
        return (date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to));
    }

    public boolean isOnDate(LocalDate date) {
        return this.reservationDate != null && this.reservationDate.equals(date);
    }
}
