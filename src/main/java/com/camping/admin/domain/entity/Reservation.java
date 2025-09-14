package com.camping.admin.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
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

    public Reservation(String customerName, LocalDate startDate, LocalDate endDate, Campsite campsite, String phoneNumber, LocalDate reservationDate, String confirmationCode) {
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.campsite = campsite;
        this.phoneNumber = phoneNumber;
        this.reservationDate = reservationDate;
        this.confirmationCode = confirmationCode;
    }

    public boolean isReservable() {
        if(this.status.equals("CONFIRMED")) {
            return false;
        }
        if(this.status.equals("CANCELLED")) {
            return true;
        }

        throw new RuntimeException("Invalid reservation status: " + this.status);
    }

    public static Reservation create(
        String customerName,
        LocalDate startDate,
        LocalDate endDate,
        Campsite campsite,
        String phoneNumber,
        LocalDate reservationDate,
        String confirmationCode
    ) {
        return new Reservation(customerName, startDate, endDate, campsite, phoneNumber, reservationDate, confirmationCode);
    }
}