package com.camping.admin.factory;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import com.camping.admin.steps.api.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RentalFactory {

    @Autowired
    private TestContext testContext;

    @Autowired
    private ProductRepository productRepository;

    public void createRental(String productName, int stockQuantity, ProductType productType) {
        Product product = createProduct(productName, stockQuantity, productType);
        Product saved = productRepository.save(product);
        testContext.setProductId(saved.getId());
        testContext.setProductName(productName);
    }

    private static Product createProduct(String productName, int stockQuantity, ProductType productType) {
        Product product = Product.builder()
                .name(productName)
                .stockQuantity(stockQuantity)
                .price(BigDecimal.valueOf(1000L))
                .productType(productType)
                .build();
        return product;
    }
}
