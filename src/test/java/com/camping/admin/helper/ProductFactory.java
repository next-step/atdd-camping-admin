package com.camping.admin.helper;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;

import java.math.BigDecimal;
import java.util.Map;

import static com.camping.admin.domain.enums.ProductType.RENTAL;
import static com.camping.admin.helper.RequestSender.post;

public class ProductFactory {

    private static final String DEFAULT_PRODUCT_NAME = "간이의자";
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("30000.00");

    /**
     * 대여용 제품 생성
     */
    public static Product productOf(Integer stockQuantity) {
        return createProduct(DEFAULT_PRODUCT_NAME, stockQuantity, DEFAULT_PRICE, RENTAL);
    }


    /**
     * POST /admin/products API를 호출하여 제품 생성
     */
    private static Product createProduct(String name,
                                         Integer stockQuantity,
                                         BigDecimal price,
                                         ProductType productType) {
        var productRequest = Map.of(
                "name", name,
                "stockQuantity", stockQuantity,
                "price", price,
                "productType", productType.name()
        );

        return post("/admin/products", productRequest).then()
                .extract()
                .as(Product.class);
    }
}
