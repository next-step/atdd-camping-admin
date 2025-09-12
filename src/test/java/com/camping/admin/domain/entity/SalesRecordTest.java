package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SalesRecordTest {

    private Product product;
    private SalesRecord salesRecord;

    @BeforeEach
    void setUp() {
        product = new Product("음료수", 100, new BigDecimal("3000"), ProductType.SALE);
        salesRecord = new SalesRecord(product, 5, new BigDecimal("15000"));
    }

    @DisplayName("판매 기록을 정상적으로 생성할 수 있다")
    @Test
    void createSalesRecord_Success() {
        assertThat(salesRecord.getProduct()).isEqualTo(product);
        assertThat(salesRecord.getQuantity()).isEqualTo(5);
        assertThat(salesRecord.getTotalPrice()).isEqualTo(new BigDecimal("15000"));
        assertThat(salesRecord.getCreatedAt()).isNotNull();
    }

    @DisplayName("상품으로부터 판매 기록을 생성할 수 있다")
    @Test
    void createFromProduct_Success() {
        SalesRecord record = SalesRecord.createFromProduct(product, 3);

        assertThat(record.getProduct()).isEqualTo(product);
        assertThat(record.getQuantity()).isEqualTo(3);
        assertThat(record.getTotalPrice()).isEqualTo(new BigDecimal("9000")); // 3000 * 3
        assertThat(record.getCreatedAt()).isNotNull();
    }

    @DisplayName("수량이 1개인 판매 기록을 상품으로부터 생성할 수 있다")
    @Test
    void createFromProduct_SingleQuantity_Success() {
        SalesRecord record = SalesRecord.createFromProduct(product, 1);

        assertThat(record.getQuantity()).isEqualTo(1);
        assertThat(record.getTotalPrice()).isEqualTo(new BigDecimal("3000"));
    }

    @DisplayName("가격이 다른 상품으로부터 판매 기록을 생성할 수 있다")
    @Test
    void createFromProduct_DifferentPrice_Success() {
        Product expensiveProduct = new Product("프리미엄 음료", 50, new BigDecimal("10000"), ProductType.SALE);

        SalesRecord record = SalesRecord.createFromProduct(expensiveProduct, 2);

        assertThat(record.getTotalPrice()).isEqualTo(new BigDecimal("20000")); // 10000 * 2
    }

    @DisplayName("특정 날짜에 생성된 판매 기록인지 확인할 수 있다")
    @Test
    void isOnDate_SameDate_ReturnsTrue() {
        LocalDate today = LocalDate.now();

        assertThat(salesRecord.isOnDate(today)).isTrue();
    }

    @DisplayName("다른 날짜에 생성된 판매 기록인지 확인할 수 있다")
    @Test
    void isOnDate_DifferentDate_ReturnsFalse() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        assertThat(salesRecord.isOnDate(yesterday)).isFalse();
    }

    @DisplayName("날짜 범위 안에 있는 판매 기록인지 확인할 수 있다")
    @Test
    void isInDateRange_WithinRange_ReturnsTrue() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(1);
        LocalDate to = today.plusDays(1);

        assertThat(salesRecord.isInDateRange(from, to)).isTrue();
    }

    @DisplayName("날짜 범위 시작일에 있는 판매 기록인지 확인할 수 있다")
    @Test
    void isInDateRange_OnStartDate_ReturnsTrue() {
        LocalDate today = LocalDate.now();
        LocalDate to = today.plusDays(1);

        assertThat(salesRecord.isInDateRange(today, to)).isTrue();
    }

    @DisplayName("날짜 범위 종료일에 있는 판매 기록인지 확인할 수 있다")
    @Test
    void isInDateRange_OnEndDate_ReturnsTrue() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(1);

        assertThat(salesRecord.isInDateRange(from, today)).isTrue();
    }

    @DisplayName("날짜 범위 밖에 있는 판매 기록인지 확인할 수 있다")
    @Test
    void isInDateRange_OutsideRange_ReturnsFalse() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(3);
        LocalDate to = today.minusDays(2);

        assertThat(salesRecord.isInDateRange(from, to)).isFalse();
    }

    @DisplayName("미래 날짜 범위에 있지 않은 판매 기록인지 확인할 수 있다")
    @Test
    void isInDateRange_FutureRange_ReturnsFalse() {
        LocalDate today = LocalDate.now();
        LocalDate from = today.plusDays(1);
        LocalDate to = today.plusDays(3);

        assertThat(salesRecord.isInDateRange(from, to)).isFalse();
    }
}
