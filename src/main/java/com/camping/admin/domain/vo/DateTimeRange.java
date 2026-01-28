package com.camping.admin.domain.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class DateTimeRange {

    private final LocalDateTime start;
    private final LocalDateTime end;

    private DateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static DateTimeRange ofDay(LocalDate date) {
        return new DateTimeRange(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );
    }

    public static DateTimeRange ofRange(LocalDate from, LocalDate to) {
        return new DateTimeRange(
            from.atStartOfDay(),
            to.plusDays(1).atStartOfDay()
        );
    }
}