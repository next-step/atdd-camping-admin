package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.domain.vo.RecordQuantity;
import com.camping.admin.exception.InsufficientStockException;
import com.camping.admin.exception.InvalidQuantityException;
import com.camping.admin.exception.ProductNotRentalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductWithStockQuantityTest {

    @Test
    @DisplayName("렌탈 상품의 재고를 RecordQuantity로 감소시킬 수 있다")
    void 렌탈_상품의_재고를_RecordQuantity로_감소시킬_수_있다() {
        // given
        Product rentalProduct = new Product("랜턴", 10, new BigDecimal("30000"), ProductType.RENTAL);

        // when
        rentalProduct.decreaseStock(new RecordQuantity(3));

        // then
        assertThat(rentalProduct.getStockQuantity().getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("렌탈 상품의 재고를 RecordQuantity로 증가시킬 수 있다")
    void 렌탈_상품의_재고를_RecordQuantity로_증가시킬_수_있다() {
        // given
        Product rentalProduct = new Product("코펠 세트", 5, new BigDecimal("20000"), ProductType.RENTAL);

        // when
        rentalProduct.increaseStock(new RecordQuantity(3));

        // then
        assertThat(rentalProduct.getStockQuantity().getQuantity()).isEqualTo(8);
    }

    @Test
    @DisplayName("판매 상품은 재고를 감소시킬 수 없다")
    void 판매_상품은_재고를_감소시킬_수_없다() {
        // given
        Product saleProduct = new Product("장작팩", 50, new BigDecimal("10000"), ProductType.SALE);

        // when & then
        assertThatThrownBy(() -> saleProduct.decreaseStock(new RecordQuantity(1)))
                .isInstanceOf(ProductNotRentalException.class)
                .hasMessage("Product is not a rental item.");

        // 재고는 변경되지 않아야 함
        assertThat(saleProduct.getStockQuantity().getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("렌탈 상품의 재고가 부족하면 StockQuantity에서 예외가 발생한다")
    void 렌탈_상품의_재고가_부족하면_StockQuantity에서_예외가_발생한다() {
        // given
        Product rentalProduct = new Product("의자", 3, new BigDecimal("15000"), ProductType.RENTAL);

        // when & then
        assertThatThrownBy(() -> rentalProduct.decreaseStock(new RecordQuantity(5)))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Insufficient stock. Current: 3, Required: 5");

        // 재고는 변경되지 않아야 함
        assertThat(rentalProduct.getStockQuantity().getQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("잘못된 RecordQuantity로 재고 조작 시 예외가 발생한다")
    void 잘못된_RecordQuantity로_재고_조작_시_예외가_발생한다() {
        // given
        Product rentalProduct = new Product("테이블", 10, new BigDecimal("25000"), ProductType.RENTAL);

        // when & then - 음수 수량
        assertThatThrownBy(() -> rentalProduct.decreaseStock(new RecordQuantity(-1)))
                .isInstanceOf(InvalidQuantityException.class);

        // when & then - 0 수량
        assertThatThrownBy(() -> rentalProduct.decreaseStock(new RecordQuantity(0)))
                .isInstanceOf(InvalidQuantityException.class);
    }

    @Test
    @DisplayName("StockQuantity가 0인 상품은 재고 감소할 수 없다")
    void StockQuantity가_0인_상품은_재고_감소할_수_없다() {
        // given
        Product rentalProduct = new Product("품절 상품", 0, new BigDecimal("10000"), ProductType.RENTAL);

        // when & then
        assertThatThrownBy(() -> rentalProduct.decreaseStock(new RecordQuantity(1)))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Insufficient stock. Current: 0, Required: 1");
    }

    @Test
    @DisplayName("StockQuantity를 정확히 요청 수량만큼 감소시킬 수 있다")
    void StockQuantity를_정확히_요청_수량만큼_감소시킬_수_있다() {
        // given
        Product rentalProduct = new Product("버너", 5, new BigDecimal("18000"), ProductType.RENTAL);

        // when
        rentalProduct.decreaseStock(new RecordQuantity(5));

        // then
        assertThat(rentalProduct.getStockQuantity().getQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("상품 생성 시 StockQuantity가 올바르게 설정된다")
    void 상품_생성_시_StockQuantity가_올바르게_설정된다() {
        // given
        String name = "캠핑 랜턴";
        Integer stockQuantity = 15;
        BigDecimal price = new BigDecimal("45000.50");
        ProductType productType = ProductType.RENTAL;

        // when
        Product product = new Product(name, stockQuantity, price, productType);

        // then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getStockQuantity().getQuantity()).isEqualTo(stockQuantity);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getProductType()).isEqualTo(productType);
        assertThat(product.getId()).isNull(); // JPA 저장 전이므로 null
    }

    @Test
    @DisplayName("음수 재고로 상품 생성 시 예외가 발생한다")
    void 음수_재고로_상품_생성_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> new Product("잘못된 상품", -5, new BigDecimal("10000"), ProductType.RENTAL))
                .isInstanceOf(InvalidQuantityException.class)
                .hasMessage("Stock quantity cannot be negative. Provided: -5");
    }
}