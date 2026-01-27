package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.CommonErrorCode;
import com.camping.admin.domain.exception.ReservationErrorCode;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StayPeriod {

    private LocalDate startDate;
    private LocalDate endDate;

    public StayPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw CommonErrorCode.REQUIRED.forClass(StayPeriod.class);
        }
        if (endDate.isBefore(startDate)) {
            throw ReservationErrorCode.END_DATE_BEFORE_START.with();
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long calculateNights() {
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        return nights < 1 ? 1 : nights;
    }
}