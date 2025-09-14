package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.InsufficientStockException;
import com.camping.admin.exception.ProductNotRentalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("렌탈 상품의 재고를 감소시킬 수 있다")
    void 렌탈_상품의_재고를_감소시킬_수_있다() {
        // given
        Product rentalProduct = new Product("랜턴", 10, new BigDecimal("30000"), ProductType.RENTAL);

        // when
        rentalProduct.decreaseStock(3);

        // then
        assertThat(rentalProduct.getStockQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("렌탈 상품의 재고를 전체 만큼 감소시킬 수 있다")
    void 렌탈_상품의_재고를_전체_만큼_감소시킬_수_있다() {
        // given
        Product rentalProduct = new Product("코펠 세트", 5, new BigDecimal("20000"), ProductType.RENTAL);

        // when
        rentalProduct.decreaseStock(5);

        // then
        assertThat(rentalProduct.getStockQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("판매 상품은 재고를 감소시킬 수 없다")
    void 판매_상품은_재고를_감소시킬_수_없다() {
        // given
        Product saleProduct = new Product("장작팩", 50, new BigDecimal("10000"), ProductType.SALE);

        // when & then
        assertThatThrownBy(() -> saleProduct.decreaseStock(1))
                .isInstanceOf(ProductNotRentalException.class)
                .hasMessage("Product is not a rental item.");

        // 재고는 변경되지 않아야 함
        assertThat(saleProduct.getStockQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("렌탈 상품의 재고가 부족하면 감소시킬 수 없다")
    void 렌탈_상품의_재고가_부족하면_감소시킬_수_없다() {
        // given
        Product rentalProduct = new Product("의자", 3, new BigDecimal("15000"), ProductType.RENTAL);

        // when & then
        assertThatThrownBy(() -> rentalProduct.decreaseStock(5))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Not enough stock for product 의자");

        // 재고는 변경되지 않아야 함
        assertThat(rentalProduct.getStockQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("렌탈 상품의 재고가 정확히 요청 수량과 같으면 감소시킬 수 있다")
    void 렌탈_상품의_재고가_정확히_요청_수량과_같으면_감소시킬_수_있다() {
        // given
        Product rentalProduct = new Product("테이블", 2, new BigDecimal("25000"), ProductType.RENTAL);

        // when
        rentalProduct.decreaseStock(2);

        // then
        assertThat(rentalProduct.getStockQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("0 수량으로 재고 감소 시도해도 정상 처리된다")
    void 영_수량으로_재고_감소_시도해도_정상_처리된다() {
        // given
        Product rentalProduct = new Product("버너", 5, new BigDecimal("18000"), ProductType.RENTAL);
        int originalStock = rentalProduct.getStockQuantity();

        // when
        rentalProduct.decreaseStock(0);

        // then
        assertThat(rentalProduct.getStockQuantity()).isEqualTo(originalStock);
    }

    @Test
    @DisplayName("음수 수량으로 재고 감소 시도해도 정상 처리된다")
    void 음수_수량으로_재고_감소_시도해도_정상_처리된다() {
        // given
        Product rentalProduct = new Product("취사도구 세트", 10, new BigDecimal("12000"), ProductType.RENTAL);

        // when
        rentalProduct.decreaseStock(-2);

        // then
        // 음수 감소는 실제로 재고가 증가함
        assertThat(rentalProduct.getStockQuantity()).isEqualTo(12);
    }

    @Test
    @DisplayName("상품 생성 시 모든 속성이 올바르게 설정된다")
    void 상품_생성_시_모든_속성이_올바르게_설정된다() {
        // given
        String name = "캠핑 랜턴";
        Integer stockQuantity = 15;
        BigDecimal price = new BigDecimal("45000.50");
        ProductType productType = ProductType.RENTAL;

        // when
        Product product = new Product(name, stockQuantity, price, productType);

        // then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getStockQuantity()).isEqualTo(stockQuantity);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getProductType()).isEqualTo(productType);
        assertThat(product.getId()).isNull(); // JPA 저장 전이므로 null
    }

    @Test
    @DisplayName("판매 상품 생성이 정상적으로 동작한다")
    void 판매_상품_생성이_정상적으로_동작한다() {
        // given & when
        Product saleProduct = new Product("생수 2L", 100, new BigDecimal("2000"), ProductType.SALE);

        // then
        assertThat(saleProduct.getProductType()).isEqualTo(ProductType.SALE);
        assertThat(saleProduct.getName()).isEqualTo("생수 2L");
        assertThat(saleProduct.getStockQuantity()).isEqualTo(100);
        assertThat(saleProduct.getPrice()).isEqualTo(new BigDecimal("2000"));
    }

    @Test
    @DisplayName("재고가 0인 렌탈 상품은 재고 감소할 수 없다")
    void 재고가_영인_렌탈_상품은_재고_감소할_수_없다() {
        // given
        Product rentalProduct = new Product("품절 상품", 0, new BigDecimal("10000"), ProductType.RENTAL);

        // when & then
        assertThatThrownBy(() -> rentalProduct.decreaseStock(1))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Not enough stock for product 품절 상품");
    }
}