package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ReservationStatus;
import com.camping.admin.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.camping.admin.domain.enums.ReservationStatus.*;

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


    /**
     * 예약 상태 변경
     * - 도메인 규칙을 엔티티가 직접 보호 (Tell, Don't Ask)
     */
    public void changeStatus(ReservationStatus newStatus) {
        validateStatusChange(newStatus);
        this.status = newStatus;
    }

    private void validateStatusChange(ReservationStatus newStatus) {
        if (this.status == CANCELLED) {
            throw new BusinessException("이미 취소된 예약은 복구할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (this.status == CHECKED_IN && newStatus == CANCELLED) {
            throw new BusinessException("체크인된 예약은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (this.status == CHECKED_OUT && newStatus == CANCELLED) {
            throw new BusinessException("이미 완료된 예약은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (this.status == REJECTED && newStatus == CANCELLED) {
            throw new BusinessException("이미 거절된 예약은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isCanceled() {
        return status == CANCELLED;
    }

    /**
     * 예약 수익 계산 (1박당 50,000원)
     */
    public BigDecimal calculateRevenue() {
        long nights = ChronoUnit.DAYS.between(this.startDate, this.endDate);
        if (nights < 1) {
            nights = 1;
        }
        return new BigDecimal(nights).multiply(new BigDecimal("50000"));
    }
}