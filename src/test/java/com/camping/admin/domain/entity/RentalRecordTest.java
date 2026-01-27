package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RentalRecordTest {

    private Product rentalProduct;

    @BeforeEach
    void setUp() {
        rentalProduct = new Product("텐트", 10, new BigDecimal("5000"), ProductType.RENTAL);
    }

    @Nested
    @DisplayName("생성")
    class Create {

        @Test
        @DisplayName("대여 기록 생성 시 재고가 감소한다")
        void createDecreasesStock() {
            int initialStock = rentalProduct.getStock().getQuantity();

            new RentalRecord(null, rentalProduct, 3);

            assertThat(rentalProduct.getStock().getQuantity()).isEqualTo(initialStock - 3);
        }

        @Test
        @DisplayName("대여 불가 상품으로 생성하면 예외가 발생한다")
        void createWithNonRentableProduct() {
            Product saleProduct = new Product("판매상품", 10, new BigDecimal("5000"), ProductType.SALE);

            assertThatThrownBy(() -> new RentalRecord(null, saleProduct, 1))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("재고보다 많이 대여하면 예외가 발생한다")
        void createWithInsufficientStock() {
            assertThatThrownBy(() -> new RentalRecord(null, rentalProduct, 100))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("markAsReturned")
    class MarkAsReturned {

        @Test
        @DisplayName("반납 처리 시 재고가 증가한다")
        void markAsReturnedIncreasesStock() {
            RentalRecord record = new RentalRecord(null, rentalProduct, 3);
            int stockAfterRental = rentalProduct.getStock().getQuantity();

            record.markAsReturned();

            assertThat(rentalProduct.getStock().getQuantity()).isEqualTo(stockAfterRental + 3);
            assertThat(record.getIsReturned()).isTrue();
        }

        @Test
        @DisplayName("이미 반납된 기록을 다시 반납하면 예외가 발생한다")
        void markAsReturnedTwice() {
            RentalRecord record = new RentalRecord(null, rentalProduct, 3);
            record.markAsReturned();

            assertThatThrownBy(record::markAsReturned)
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("calculateRevenue")
    class CalculateRevenue {

        @ParameterizedTest(name = "가격 {0}원 x 수량 {1} = {2}원")
        @CsvSource({
                "5000, 1, 5000",
                "5000, 3, 15000",
                "10000, 5, 50000"
        })
        @DisplayName("대여 매출을 계산할 수 있다")
        void calculateRevenue(int price, int quantity, int expected) {
            Product product = new Product("상품", 100, new BigDecimal(price), ProductType.RENTAL);
            RentalRecord record = new RentalRecord(null, product, quantity);

            assertThat(record.calculateRevenue()).isEqualByComparingTo(BigDecimal.valueOf(expected));
        }
    }
}