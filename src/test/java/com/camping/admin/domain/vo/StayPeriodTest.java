package com.camping.admin.domain.vo;

import com.camping.admin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StayPeriodTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @Test
        @DisplayName("유효한 기간으로 생성할 수 있다")
        void createWithValidPeriod() {
            LocalDate start = LocalDate.of(2024, 1, 1);
            LocalDate end = LocalDate.of(2024, 1, 3);

            StayPeriod period = new StayPeriod(start, end);

            assertThat(period.getStartDate()).isEqualTo(start);
            assertThat(period.getEndDate()).isEqualTo(end);
        }

        @Test
        @DisplayName("시작일과 종료일이 같아도 생성할 수 있다")
        void createWithSameDate() {
            LocalDate date = LocalDate.of(2024, 1, 1);

            StayPeriod period = new StayPeriod(date, date);

            assertThat(period.getStartDate()).isEqualTo(date);
            assertThat(period.getEndDate()).isEqualTo(date);
        }

        @Test
        @DisplayName("종료일이 시작일보다 이전이면 예외가 발생한다")
        void createWithEndBeforeStart() {
            LocalDate start = LocalDate.of(2024, 1, 3);
            LocalDate end = LocalDate.of(2024, 1, 1);

            assertThatThrownBy(() -> new StayPeriod(start, end))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("시작일 또는 종료일이 null이면 예외가 발생한다")
        void createWithNull() {
            LocalDate date = LocalDate.of(2024, 1, 1);

            assertThatThrownBy(() -> new StayPeriod(null, date))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> new StayPeriod(date, null))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("calculateNights")
    class CalculateNights {

        @ParameterizedTest(name = "{0} ~ {1} = {2}박")
        @CsvSource({
                "2024-01-01, 2024-01-01, 1",
                "2024-01-01, 2024-01-02, 1",
                "2024-01-01, 2024-01-03, 2",
                "2024-01-01, 2024-01-10, 9"
        })
        @DisplayName("숙박일수를 계산할 수 있다")
        void calculateNights(String startStr, String endStr, long expected) {
            LocalDate start = LocalDate.parse(startStr);
            LocalDate end = LocalDate.parse(endStr);
            StayPeriod period = new StayPeriod(start, end);

            assertThat(period.calculateNights()).isEqualTo(expected);
        }
    }
}