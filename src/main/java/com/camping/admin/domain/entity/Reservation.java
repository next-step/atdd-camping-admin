package com.camping.admin.domain.entity;

import com.camping.admin.domain.RevenueSource;
import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.domain.exception.CommonErrorCode;
import com.camping.admin.domain.exception.ReservationErrorCode;
import com.camping.admin.domain.vo.ConfirmationCode;
import com.camping.admin.domain.vo.PhoneNumber;
import com.camping.admin.domain.vo.ReservationTiming;
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
public class Reservation implements RevenueSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "reservationDate", column = @Column(name = "reservation_date")),
        @AttributeOverride(name = "stayPeriod.startDate", column = @Column(name = "start_date", nullable = false)),
        @AttributeOverride(name = "stayPeriod.endDate", column = @Column(name = "end_date", nullable = false))
    })
    private ReservationTiming timing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campsite_id", nullable = false)
    private Campsite campsite;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number"))
    private PhoneNumber phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "confirmation_code", length = 6))
    private ConfirmationCode confirmationCode;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReservationStatus.CONFIRMED;
        }
        if (this.confirmationCode == null) {
            this.confirmationCode = ConfirmationCode.generate();
        }
    }

    public Reservation(String customerName, LocalDate startDate, LocalDate endDate, Campsite campsite) {
        this.customerName = customerName;
        this.timing = new ReservationTiming(LocalDate.now(), startDate, endDate);
        this.campsite = campsite;
    }

    // 기존 코드 호환을 위한 위임 메서드
    public LocalDate getStartDate() {
        return timing != null ? timing.getStartDate() : null;
    }

    public LocalDate getEndDate() {
        return timing != null ? timing.getEndDate() : null;
    }

    public LocalDate getReservationDate() {
        return timing != null ? timing.getReservationDate() : null;
    }

    public void updateStatus(ReservationStatus newStatus) {
        if (newStatus == null) {
            throw CommonErrorCode.REQUIRED.withDomain("상태");
        }
        if (this.status.isFinal()) {
            throw ReservationErrorCode.ALREADY_FINAL.with(this.status);
        }
        this.status = newStatus;
    }

    public void updateStatus(String newStatus) {
        try {
            updateStatus(ReservationStatus.valueOf(newStatus));
        } catch (IllegalArgumentException e) {
            throw ReservationErrorCode.INVALID_STATUS.with(newStatus);
        }
    }

    public long calculateNights() {
        return timing.calculateNights();
    }

    @Override
    public BigDecimal calculateRevenue() {
        return BigDecimal.valueOf(calculateNights())
                         .multiply(new BigDecimal("50000"));
    }
}