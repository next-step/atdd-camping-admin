package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public boolean isCanceled() {
        return ReservationStatus.CANCELLED == this.status;
    }

    /**
     * 사용자 예약 상태 변경
     * @param targetStatus
     * @return 상태 변경 결과, true면 성공, false 면 실패
     */
    public boolean updateStatus(ReservationStatus targetStatus) {
        if (targetStatus == ReservationStatus.CHECKED_IN) {
            if (isCanceled()) {
                return false;
            }
        }
        this.status = targetStatus;
        return true;
    }
}
