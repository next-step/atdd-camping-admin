package com.camping.admin.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeRangeTest {

    @Nested
    @DisplayName("ofDay")
    class OfDay {

        @Test
        @DisplayName("하루의 시작과 끝 시간을 반환한다")
        void createDayRange() {
            LocalDate date = LocalDate.of(2024, 1, 15);

            DateTimeRange range = DateTimeRange.ofDay(date);

            assertThat(range.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 0, 0, 0));
            assertThat(range.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 0, 0, 0));
        }

        @Test
        @DisplayName("월말에도 정상 동작한다")
        void createDayRangeAtEndOfMonth() {
            LocalDate date = LocalDate.of(2024, 1, 31);

            DateTimeRange range = DateTimeRange.ofDay(date);

            assertThat(range.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 31, 0, 0, 0));
            assertThat(range.getEnd()).isEqualTo(LocalDateTime.of(2024, 2, 1, 0, 0, 0));
        }

        @Test
        @DisplayName("연말에도 정상 동작한다")
        void createDayRangeAtEndOfYear() {
            LocalDate date = LocalDate.of(2024, 12, 31);

            DateTimeRange range = DateTimeRange.ofDay(date);

            assertThat(range.getStart()).isEqualTo(LocalDateTime.of(2024, 12, 31, 0, 0, 0));
            assertThat(range.getEnd()).isEqualTo(LocalDateTime.of(2025, 1, 1, 0, 0, 0));
        }
    }

    @Nested
    @DisplayName("ofRange")
    class OfRange {

        @Test
        @DisplayName("기간의 시작과 끝 시간을 반환한다")
        void createRange() {
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 1, 15);

            DateTimeRange range = DateTimeRange.ofRange(from, to);

            assertThat(range.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 0, 0, 0));
            assertThat(range.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 0, 0, 0));
        }

        @Test
        @DisplayName("시작일과 종료일이 같으면 하루 범위를 반환한다")
        void createRangeWithSameDate() {
            LocalDate date = LocalDate.of(2024, 1, 15);

            DateTimeRange range = DateTimeRange.ofRange(date, date);

            assertThat(range.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 15, 0, 0, 0));
            assertThat(range.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 16, 0, 0, 0));
        }

        @Test
        @DisplayName("월을 넘어가는 기간도 정상 동작한다")
        void createRangeAcrossMonths() {
            LocalDate from = LocalDate.of(2024, 1, 25);
            LocalDate to = LocalDate.of(2024, 2, 5);

            DateTimeRange range = DateTimeRange.ofRange(from, to);

            assertThat(range.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 25, 0, 0, 0));
            assertThat(range.getEnd()).isEqualTo(LocalDateTime.of(2024, 2, 6, 0, 0, 0));
        }
    }
}