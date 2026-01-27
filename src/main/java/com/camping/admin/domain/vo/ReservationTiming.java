package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.CommonErrorCode;
import com.camping.admin.domain.exception.ReservationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationTiming {

    private LocalDate reservationDate;

    @Embedded
    private StayPeriod stayPeriod;

    public ReservationTiming(LocalDate reservationDate, LocalDate startDate, LocalDate endDate) {
        StayPeriod stayPeriod = new StayPeriod(startDate, endDate);
        validateTiming(reservationDate, stayPeriod);
        this.reservationDate = reservationDate;
        this.stayPeriod = stayPeriod;
    }

    public ReservationTiming(LocalDate reservationDate, StayPeriod stayPeriod) {
        validateTiming(reservationDate, stayPeriod);
        this.reservationDate = reservationDate;
        this.stayPeriod = stayPeriod;
    }

    private void validateTiming(LocalDate reservationDate, StayPeriod stayPeriod) {
        if (reservationDate == null) {
            throw CommonErrorCode.REQUIRED.forClass(ReservationTiming.class);
        }
        if (reservationDate.isAfter(stayPeriod.getStartDate())) {
            throw ReservationErrorCode.RESERVATION_DATE_AFTER_START.with();
        }
    }

    public LocalDate getStartDate() {
        return stayPeriod.getStartDate();
    }

    public LocalDate getEndDate() {
        return stayPeriod.getEndDate();
    }

    public long calculateNights() {
        return stayPeriod.calculateNights();
    }
}