package com.camping.admin.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTypeTest {

    @Nested
    @DisplayName("isRentable")
    class IsRentable {

        @Test
        @DisplayName("RENTAL 타입은 대여 가능하다")
        void rentalIsRentable() {
            assertThat(ProductType.RENTAL.isRentable()).isTrue();
        }

        @Test
        @DisplayName("SALE 타입은 대여 불가능하다")
        void saleIsNotRentable() {
            assertThat(ProductType.SALE.isRentable()).isFalse();
        }
    }

    @Nested
    @DisplayName("isSellable")
    class IsSellable {

        @Test
        @DisplayName("SALE 타입은 판매 가능하다")
        void saleIsSellable() {
            assertThat(ProductType.SALE.isSellable()).isTrue();
        }

        @Test
        @DisplayName("RENTAL 타입은 판매 불가능하다")
        void rentalIsNotSellable() {
            assertThat(ProductType.RENTAL.isSellable()).isFalse();
        }
    }

    @Nested
    @DisplayName("상호 배타적 검증")
    class MutualExclusivity {

        @ParameterizedTest(name = "{0}은 rentable과 sellable 중 하나만 true이다")
        @EnumSource(ProductType.class)
        @DisplayName("모든 타입은 대여 가능 또는 판매 가능 중 하나만 true이다")
        void mutuallyExclusive(ProductType type) {
            boolean rentable = type.isRentable();
            boolean sellable = type.isSellable();

            // XOR: 둘 중 하나만 true
            assertThat(rentable ^ sellable).isTrue();
        }
    }
}