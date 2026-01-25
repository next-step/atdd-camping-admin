package com.camping.admin.factory;

import com.camping.admin.domain.entity.Product;
import com.camping.admin.domain.enums.ProductType;
import com.camping.admin.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RentalFactory {

    @Autowired
    private ProductRepository productRepository;

    public Product createRental(String productName, int stockQuantity, ProductType productType) {
        Product product = createProduct(productName, stockQuantity, productType);
        return productRepository.save(product);
    }

    private static Product createProduct(String productName, int stockQuantity, ProductType productType) {
        return Product.builder()
                .name(productName)
                .stockQuantity(stockQuantity)
                .price(BigDecimal.valueOf(1000L))
                .productType(productType)
                .build();
    }
}
