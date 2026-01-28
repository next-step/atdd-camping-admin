package com.camping.admin.steps;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.camping.admin.apiExtractableresponse.ProductApiExtractableResponse.상품을_등록한다;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 상품 관련 인수 테스트에서 공통으로 사용되는 헬퍼 클래스
 */
@Component
public class ProductTestHelper {

    @Autowired
    private ProductRepository productRepository;

    private long productCountBeforeRequest;

    // ==================== 상품 등록 (When) ====================

    public void 상품_정보로_등록을_요청한다(String name, int stockQuantity, BigDecimal price, String productType) {
        this.productCountBeforeRequest = productRepository.count();

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", name);
        productData.put("stockQuantity", stockQuantity);
        productData.put("price", price);
        productData.put("productType", productType);

        CommonHooks.lastResponse = 상품을_등록한다(productData);
    }

    public void 상품명_없이_상품_등록을_요청한다() {
        this.productCountBeforeRequest = productRepository.count();

        Map<String, Object> productData = new HashMap<>();
        productData.put("stockQuantity", 10);
        productData.put("price", new BigDecimal("10000"));
        productData.put("productType", "SALE");

        CommonHooks.lastResponse = 상품을_등록한다(productData);
    }

    public void 유효하지_않은_유형으로_상품_등록을_요청한다() {
        this.productCountBeforeRequest = productRepository.count();

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "테스트 상품");
        productData.put("stockQuantity", 10);
        productData.put("price", new BigDecimal("10000"));
        productData.put("productType", "INVALID_TYPE");

        CommonHooks.lastResponse = 상품을_등록한다(productData);
    }

    public void 가격을_음수로_상품_등록을_요청한다(int price) {
        this.productCountBeforeRequest = productRepository.count();

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "테스트 상품");
        productData.put("stockQuantity", 10);
        productData.put("price", new BigDecimal(price));
        productData.put("productType", "SALE");

        CommonHooks.lastResponse = 상품을_등록한다(productData);
    }

    public void 재고를_음수로_상품_등록을_요청한다(int stockQuantity) {
        this.productCountBeforeRequest = productRepository.count();

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "테스트 상품");
        productData.put("stockQuantity", stockQuantity);
        productData.put("price", new BigDecimal("10000"));
        productData.put("productType", "SALE");

        CommonHooks.lastResponse = 상품을_등록한다(productData);
    }

    public void 상품명을_빈_값으로_상품_등록을_요청한다() {
        this.productCountBeforeRequest = productRepository.count();

        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "");
        productData.put("stockQuantity", 10);
        productData.put("price", new BigDecimal("10000"));
        productData.put("productType", "SALE");

        CommonHooks.lastResponse = 상품을_등록한다(productData);
    }

    // ==================== 검증 (Then) ====================

    public void 상품이_등록되었는지_검증한다() {
        long currentCount = productRepository.count();
        assertThat(currentCount).isEqualTo(productCountBeforeRequest + 1);
    }

    public void 상품이_등록되지_않았는지_검증한다() {
        long currentCount = productRepository.count();
        assertThat(currentCount).isEqualTo(productCountBeforeRequest);
    }

    public void 상품_목록에_포함되는지_검증한다(String productName) {
        List<Product> products = productRepository.findAll();
        assertThat(products)
                .extracting(Product::getName)
                .contains(productName);
    }
}