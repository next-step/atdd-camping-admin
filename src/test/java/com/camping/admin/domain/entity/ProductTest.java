package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("텐트", 10, new BigDecimal("50000"), ProductType.RENTAL);
    }

    @DisplayName("재고를 정상적으로 감소시킬 수 있다")
    @Test
    void decreaseStock_Success() {
        // when
        product.decreaseStock(3);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(7);
    }

    @DisplayName("재고보다 많은 수량을 감소시킬 때 예외가 발생한다")
    @Test
    void decreaseStock_NotEnoughStock_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> product.decreaseStock(15))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Not enough stock");
    }

    @DisplayName("음수 수량으로 재고를 감소시킬 때 예외가 발생한다")
    @Test
    void decreaseStock_NegativeQuantity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> product.decreaseStock(-1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Quantity must be greater than 0");
    }

    @DisplayName("null 수량으로 재고를 감소시킬 때 예외가 발생한다")
    @Test
    void decreaseStock_NullQuantity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> product.decreaseStock(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Quantity must be greater than 0");
    }

    @DisplayName("재고를 정상적으로 증가시킬 수 있다")
    @Test
    void increaseStock_Success() {
        // when
        product.increaseStock(5);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    @DisplayName("음수 수량으로 재고를 증가시킬 때 예외가 발생한다")
    @Test
    void increaseStock_NegativeQuantity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> product.increaseStock(-1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Quantity must be greater than 0");
    }

    @DisplayName("렌탈 타입 상품인지 확인할 수 있다")
    @Test
    void isRentalType_RentalProduct_ReturnsTrue() {
        // when & then
        assertThat(product.isRentalType()).isTrue();
    }

    @DisplayName("판매 타입 상품인지 확인할 수 있다")
    @Test
    void isRentalType_SaleProduct_ReturnsFalse() {
        // given
        Product saleProduct = new Product("음료수", 20, new BigDecimal("3000"), ProductType.SALE);

        // when & then
        assertThat(saleProduct.isRentalType()).isFalse();
    }

    @DisplayName("총 가격을 정상적으로 계산할 수 있다")
    @Test
    void calculateTotalPrice_Success() {
        // when
        BigDecimal totalPrice = product.calculateTotalPrice(3);

        // then
        assertThat(totalPrice).isEqualTo(new BigDecimal("150000"));
    }

    @DisplayName("음수 수량으로 총 가격을 계산할 때 예외가 발생한다")
    @Test
    void calculateTotalPrice_NegativeQuantity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> product.calculateTotalPrice(-1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Quantity must be greater than 0");
    }

    @DisplayName("0 수량으로 총 가격을 계산할 때 예외가 발생한다")
    @Test
    void calculateTotalPrice_ZeroQuantity_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> product.calculateTotalPrice(0))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Quantity must be greater than 0");
    }
}