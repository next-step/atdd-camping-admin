package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Nested
    @DisplayName("생성")
    class Create {

        @Test
        @DisplayName("유효한 값으로 생성할 수 있다")
        void createWithValidValues() {
            Product product = new Product("텐트", 10, new BigDecimal("50000"), ProductType.RENTAL);

            assertThat(product.getName()).isEqualTo("텐트");
            assertThat(product.getStock().getQuantity()).isEqualTo(10);
            assertThat(product.getPrice()).isEqualByComparingTo("50000");
            assertThat(product.getProductType()).isEqualTo(ProductType.RENTAL);
        }

        @Test
        @DisplayName("null 값은 기본값으로 대체된다")
        void createWithNullValues() {
            Product product = new Product("상품", null, null, null);

            assertThat(product.getStock().getQuantity()).isZero();
            assertThat(product.getPrice()).isEqualByComparingTo("0");
            assertThat(product.getProductType()).isEqualTo(ProductType.SALE);
        }
    }

    @Nested
    @DisplayName("isRentable / validateRentable")
    class Rentable {

        @ParameterizedTest(name = "{0} -> isRentable: {1}")
        @CsvSource({
                "RENTAL, true",
                "SALE, false"
        })
        @DisplayName("상품 타입에 따라 대여 가능 여부가 결정된다")
        void isRentable(ProductType type, boolean expected) {
            Product product = new Product("상품", 10, new BigDecimal("10000"), type);

            assertThat(product.isRentable()).isEqualTo(expected);
        }

        @Test
        @DisplayName("대여 불가 상품에서 validateRentable 호출 시 예외 발생")
        void validateRentableThrows() {
            Product product = new Product("판매 상품", 10, new BigDecimal("10000"), ProductType.SALE);

            assertThatThrownBy(product::validateRentable)
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("대여 가능 상품에서 validateRentable 호출 시 정상")
        void validateRentableSuccess() {
            Product product = new Product("대여 상품", 10, new BigDecimal("10000"), ProductType.RENTAL);

            product.validateRentable(); // 예외 없음
        }
    }

    @Nested
    @DisplayName("isSellable / validateSellable")
    class Sellable {

        @ParameterizedTest(name = "{0} -> isSellable: {1}")
        @CsvSource({
                "SALE, true",
                "RENTAL, false"
        })
        @DisplayName("상품 타입에 따라 판매 가능 여부가 결정된다")
        void isSellable(ProductType type, boolean expected) {
            Product product = new Product("상품", 10, new BigDecimal("10000"), type);

            assertThat(product.isSellable()).isEqualTo(expected);
        }

        @Test
        @DisplayName("판매 불가 상품에서 validateSellable 호출 시 예외 발생")
        void validateSellableThrows() {
            Product product = new Product("대여 상품", 10, new BigDecimal("10000"), ProductType.RENTAL);

            assertThatThrownBy(product::validateSellable)
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("재고 관리")
    class StockManagement {

        @ParameterizedTest(name = "재고 {0}에서 {1} 감소 -> {2}")
        @CsvSource({
                "10, 3, 7",
                "10, 10, 0",
                "5, 1, 4"
        })
        @DisplayName("재고를 감소시킬 수 있다")
        void decreaseStock(int initial, int amount, int expected) {
            Product product = new Product("상품", initial, new BigDecimal("10000"), ProductType.SALE);

            product.decreaseStock(amount);

            assertThat(product.getStock().getQuantity()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "재고 {0}에서 {1} 증가 -> {2}")
        @CsvSource({
                "10, 5, 15",
                "0, 10, 10"
        })
        @DisplayName("재고를 증가시킬 수 있다")
        void increaseStock(int initial, int amount, int expected) {
            Product product = new Product("상품", initial, new BigDecimal("10000"), ProductType.SALE);

            product.increaseStock(amount);

            assertThat(product.getStock().getQuantity()).isEqualTo(expected);
        }

        @Test
        @DisplayName("재고보다 많이 감소시키면 예외 발생")
        void decreaseStockMoreThanAvailable() {
            Product product = new Product("상품", 5, new BigDecimal("10000"), ProductType.SALE);

            assertThatThrownBy(() -> product.decreaseStock(10))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("null이 아닌 값만 업데이트된다")
        void updateOnlyNonNullValues() {
            Product product = new Product("원래이름", 10, new BigDecimal("10000"), ProductType.SALE);

            product.update("새이름", null, null, null);

            assertThat(product.getName()).isEqualTo("새이름");
            assertThat(product.getStock().getQuantity()).isEqualTo(10);
            assertThat(product.getPrice()).isEqualByComparingTo("10000");
            assertThat(product.getProductType()).isEqualTo(ProductType.SALE);
        }

        @Test
        @DisplayName("모든 값을 업데이트할 수 있다")
        void updateAllValues() {
            Product product = new Product("원래이름", 10, new BigDecimal("10000"), ProductType.SALE);

            product.update("새이름", 20, new BigDecimal("20000"), ProductType.RENTAL);

            assertThat(product.getName()).isEqualTo("새이름");
            assertThat(product.getStock().getQuantity()).isEqualTo(20);
            assertThat(product.getPrice()).isEqualByComparingTo("20000");
            assertThat(product.getProductType()).isEqualTo(ProductType.RENTAL);
        }
    }
}