package com.camping.admin.fixture;

import com.camping.admin.domain.entity.Campsite;
import com.camping.admin.domain.entity.Reservation;
import lombok.Builder;

import java.time.LocalDate;

public class ReservationFixture {
    public static ReservationBuilder builder() {
        return ReservationFixture.innerBuilder()
                .customerName("홍길동")
                .startDate(LocalDate.of(2024, 7, 1))
                .endDate(LocalDate.of(2024, 7, 3))
                ;
    }

    @Builder(builderMethodName = "innerBuilder")
    public static Reservation builder(
            String customerName,
            LocalDate startDate,
            LocalDate endDate,
            Campsite campsite
    ) {
        return new Reservation(
                customerName,
                startDate,
                endDate,
                campsite
        );
    }
}