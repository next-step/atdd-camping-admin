package com.camping.admin.domain.entity;

import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductParameterExtractionTest {

    @DisplayName("이름을 정상적으로 추출할 수 있다")
    @Test
    void extractName_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "텐트");

        String name = Product.extractName(body);

        assertThat(name).isEqualTo("텐트");
    }

    @DisplayName("이름이 null인 경우 예외가 발생한다")
    @Test
    void extractName_NullValue_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", null);

        assertThatThrownBy(() -> Product.extractName(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Product name cannot be null");
    }

    @DisplayName("이름 키가 없는 경우 예외가 발생한다")
    @Test
    void extractName_NoKey_ThrowsException() {
        Map<String, Object> body = new HashMap<>();

        assertThatThrownBy(() -> Product.extractName(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Product name is required");
    }

    @DisplayName("재고 수량을 정상적으로 추출할 수 있다")
    @Test
    void extractStockQuantity_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("stockQuantity", 10);

        Integer stockQuantity = Product.extractStockQuantity(body);

        assertThat(stockQuantity).isEqualTo(10);
    }

    @DisplayName("재고 수량이 문자열인 경우 파싱해서 반환한다")
    @Test
    void extractStockQuantity_StringValue_ParsesCorrectly() {
        Map<String, Object> body = new HashMap<>();
        body.put("stockQuantity", "15");

        Integer stockQuantity = Product.extractStockQuantity(body);

        assertThat(stockQuantity).isEqualTo(15);
    }

    @DisplayName("유효하지 않은 재고 수량 문자열인 경우 예외가 발생한다")
    @Test
    void extractStockQuantity_InvalidStringValue_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("stockQuantity", "invalid");

        assertThatThrownBy(() -> Product.extractStockQuantity(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid stock quantity format: invalid");
    }

    @DisplayName("재고 수량이 null인 경우 0을 반환한다")
    @Test
    void extractStockQuantity_NullValue_ReturnsZero() {
        Map<String, Object> body = new HashMap<>();
        body.put("stockQuantity", null);

        Integer stockQuantity = Product.extractStockQuantity(body);

        assertThat(stockQuantity).isEqualTo(0);
    }

    @DisplayName("재고 수량 키가 없는 경우 0을 반환한다")
    @Test
    void extractStockQuantity_NoKey_ReturnsZero() {
        Map<String, Object> body = new HashMap<>();

        Integer stockQuantity = Product.extractStockQuantity(body);

        assertThat(stockQuantity).isEqualTo(0);
    }

    @DisplayName("가격을 정상적으로 추출할 수 있다")
    @Test
    void extractPrice_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("price", 50000);

        BigDecimal price = Product.extractPrice(body);

        assertThat(price).isEqualTo(new BigDecimal("50000"));
    }

    @DisplayName("가격이 문자열인 경우 파싱해서 반환한다")
    @Test
    void extractPrice_StringValue_ParsesCorrectly() {
        Map<String, Object> body = new HashMap<>();
        body.put("price", "75000");

        BigDecimal price = Product.extractPrice(body);

        assertThat(price).isEqualTo(new BigDecimal("75000"));
    }

    @DisplayName("유효하지 않은 가격 문자열인 경우 예외가 발생한다")
    @Test
    void extractPrice_InvalidStringValue_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("price", "invalid");

        assertThatThrownBy(() -> Product.extractPrice(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid price format: invalid");
    }

    @DisplayName("가격이 null인 경우 0을 반환한다")
    @Test
    void extractPrice_NullValue_ReturnsZero() {
        Map<String, Object> body = new HashMap<>();
        body.put("price", null);

        BigDecimal price = Product.extractPrice(body);

        assertThat(price).isEqualTo(BigDecimal.ZERO);
    }

    @DisplayName("상품 타입을 정상적으로 추출할 수 있다")
    @Test
    void extractProductType_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("productType", "RENTAL");

        ProductType productType = Product.extractProductType(body);

        assertThat(productType).isEqualTo(ProductType.RENTAL);
    }

    @DisplayName("상품 타입이 null인 경우 SALE을 반환한다")
    @Test
    void extractProductType_NullValue_ReturnsSale() {
        Map<String, Object> body = new HashMap<>();
        body.put("productType", null);

        ProductType productType = Product.extractProductType(body);

        assertThat(productType).isEqualTo(ProductType.SALE);
    }

    @DisplayName("잘못된 상품 타입인 경우 예외가 발생한다")
    @Test
    void extractProductType_InvalidValue_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("productType", "INVALID");

        assertThatThrownBy(() -> Product.extractProductType(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid product type: INVALID");
    }

    @DisplayName("Map으로부터 상품 정보를 업데이트할 수 있다")
    @Test
    void updateFromMap_Success() {
        Product product = new Product("원래 이름", 5, new BigDecimal("10000"), ProductType.SALE);
        Map<String, Object> body = new HashMap<>();
        body.put("name", "새 이름");
        body.put("stockQuantity", 20);
        body.put("price", "30000");
        body.put("productType", "RENTAL");

        product.updateFromMap(body);

        assertThat(product.getName()).isEqualTo("새 이름");
        assertThat(product.getStockQuantity()).isEqualTo(20);
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("30000"));
        assertThat(product.getProductType()).isEqualTo(ProductType.RENTAL);
    }

    @DisplayName("빈 Map으로 업데이트해도 변경되지 않는다")
    @Test
    void updateFromMap_EmptyMap_NoChange() {
        Product product = new Product("원래 이름", 5, new BigDecimal("10000"), ProductType.SALE);
        Map<String, Object> body = new HashMap<>();

        product.updateFromMap(body);

        assertThat(product.getName()).isEqualTo("원래 이름");
        assertThat(product.getStockQuantity()).isEqualTo(5);
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("10000"));
        assertThat(product.getProductType()).isEqualTo(ProductType.SALE);
    }

    @DisplayName("null Map으로 업데이트해도 변경되지 않는다")
    @Test
    void updateFromMap_NullMap_NoChange() {
        Product product = new Product("원래 이름", 5, new BigDecimal("10000"), ProductType.SALE);

        product.updateFromMap(null);

        assertThat(product.getName()).isEqualTo("원래 이름");
        assertThat(product.getStockQuantity()).isEqualTo(5);
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("10000"));
        assertThat(product.getProductType()).isEqualTo(ProductType.SALE);
    }

    @DisplayName("유효하지 않은 재고 수량으로 업데이트 시 예외가 발생한다")
    @Test
    void updateFromMap_InvalidStockQuantity_ThrowsException() {
        Product product = new Product("원래 이름", 5, new BigDecimal("10000"), ProductType.SALE);
        Map<String, Object> body = new HashMap<>();
        body.put("stockQuantity", "invalid");

        assertThatThrownBy(() -> product.updateFromMap(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid stock quantity format during update: invalid");
    }

    @DisplayName("유효하지 않은 가격으로 업데이트 시 예외가 발생한다")
    @Test
    void updateFromMap_InvalidPrice_ThrowsException() {
        Product product = new Product("원래 이름", 5, new BigDecimal("10000"), ProductType.SALE);
        Map<String, Object> body = new HashMap<>();
        body.put("price", "invalid");

        assertThatThrownBy(() -> product.updateFromMap(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid price format during update: invalid");
    }

    @DisplayName("유효하지 않은 상품 타입으로 업데이트 시 예외가 발생한다")
    @Test
    void updateFromMap_InvalidProductType_ThrowsException() {
        Product product = new Product("원래 이름", 5, new BigDecimal("10000"), ProductType.SALE);
        Map<String, Object> body = new HashMap<>();
        body.put("productType", "INVALID");

        assertThatThrownBy(() -> product.updateFromMap(body))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid product type during update: INVALID");
    }
}
