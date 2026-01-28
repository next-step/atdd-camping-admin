package com.camping.admin.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationStatusTest {

    @Nested
    @DisplayName("isFinal")
    class IsFinal {

        @ParameterizedTest(name = "{0}은 최종 상태이다")
        @EnumSource(value = ReservationStatus.class, names = {"REJECTED", "CHECKED_OUT", "CANCELLED"})
        @DisplayName("최종 상태는 변경 불가능하다")
        void finalStatuses(ReservationStatus status) {
            assertThat(status.isFinal()).isTrue();
        }

        @ParameterizedTest(name = "{0}은 진행중 상태이다")
        @EnumSource(value = ReservationStatus.class, names = {"WAITING", "PENDING", "CONFIRMED", "CHECKED_IN"})
        @DisplayName("진행중 상태는 변경 가능하다")
        void nonFinalStatuses(ReservationStatus status) {
            assertThat(status.isFinal()).isFalse();
        }
    }

    @Nested
    @DisplayName("모든 상태가 isFinal 값을 가진다")
    class AllStatusesHaveIsFinal {

        @ParameterizedTest(name = "{0}은 isFinal 값이 정의되어 있다")
        @EnumSource(ReservationStatus.class)
        @DisplayName("모든 상태는 isFinal 값이 정의되어 있다")
        void allStatusesHaveDefinedIsFinal(ReservationStatus status) {
            // isFinal()이 예외 없이 호출되는지 확인
            boolean isFinal = status.isFinal();
            assertThat(isFinal).isNotNull();
        }
    }
}